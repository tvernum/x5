/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adjective.x5.command;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.List;
import java.util.Optional;

import org.adjective.x5.exception.BadArgumentException;
import org.adjective.x5.exception.CryptoStoreException;
import org.adjective.x5.exception.TypeConversionException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.io.encrypt.JksEncryptionInfo;
import org.adjective.x5.io.encrypt.Pkcs12EncryptionInfo;
import org.adjective.x5.types.CryptoStore;
import org.adjective.x5.types.FileType;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.types.crypto.JavaKeyStore;
import org.adjective.x5.types.crypto.Pkcs12KeyStore;
import org.adjective.x5.types.value.Password;
import org.adjective.x5.util.X5BiFunction;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilder;

public class ToCommand extends AbstractSimpleCommand {

    @Override
    public String name() {
        return "to";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception {
        requireArgumentCount(1, args);
        final X5Object value = values.pop();
        final String convertTo = args.get(0);
        final X5Object converted = convert(value, convertTo, context);
        values.push(converted);
    }

    private X5Object convert(X5Object object, String convertTo, Context context) throws X5Exception {
        Optional<FileType> fileType = FileType.parse(convertTo);
        if (fileType.isPresent()) {
            final FileType ft = fileType.get();
            return convert(object, ft, context).orElseThrow(() -> new TypeConversionException(object, ft));
        }
        throw new BadArgumentException("Unsupported target type '" + convertTo + "' for " + name(), this);
    }

    private Optional<X5Object> convert(X5Object object, FileType fileType, Context context) throws X5Exception {
        if (object.getSource().getFileType().equals(fileType)) {
            return Optional.of(object);
        }
        switch (fileType) {
            case JKS: {
                final Optional<CryptoStore> store = object.as(X5Type.STORE);
                if (store.isPresent()) {
                    return Optional.of(toJks(store.get(), context));
                } else {
                    return Optional.empty();
                }
            }
            case PKCS12: {
                final Optional<CryptoStore> store = object.as(X5Type.STORE);
                if (store.isPresent()) {
                    return Optional.of(toPkcs12(store.get(), context));
                } else {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    private CryptoStore toJks(CryptoStore originalStore, Context context) throws X5Exception {
        return convertJavaKeysStore(originalStore, context, "JKS", (ks, password) -> {
            final EncryptionInfo encryption = new JksEncryptionInfo(this.getSource(), password);
            return new JavaKeyStore(ks, originalStore.getSource().withDescriptionPrefix("to-jks").withFileType(FileType.JKS), encryption);
        });
    }

    private CryptoStore toPkcs12(CryptoStore originalStore, Context context) throws X5Exception {
        return convertJavaKeysStore(originalStore, context, "pkcs12", (ks, password) -> {
            try {
                final Pkcs12EncryptionInfo encryption = new Pkcs12EncryptionInfo(this.getSource(), OIWObjectIdentifiers.idSHA1, password);
                final PKCS12PfxPdu pfx = new PKCS12PfxPduBuilder()
                        .build(new BcPKCS12MacCalculatorBuilder(), password.chars());
                return new Pkcs12KeyStore(
                    ks,
                    pfx,
                    originalStore.getSource().withDescriptionPrefix("to-pkcs12").withFileType(FileType.PKCS12),
                    encryption
                );
            } catch (PKCSException e) {
                throw new CryptoStoreException("Failed to generate PKCS#12 keystore", e);
            }
        });
    }

    private JavaKeyStore convertJavaKeysStore(
        CryptoStore originalStore,
        Context context,
        String ksType,
        X5BiFunction<KeyStore, Password, ? extends JavaKeyStore> factory
    ) throws X5Exception {
        try {
            final KeyStore ks = KeyStore.getInstance(ksType);
            final Password password;
            if (originalStore.encryption().isEncrypted()) {
                password = originalStore.encryption().password();
            } else {
                password = context.passwords().forCommand(this);
            }
            ks.load(null, password.chars());
            final JavaKeyStore newStore = factory.apply(ks, password);

            for (var entry : originalStore.entries()) {
                newStore.addEntry(entry, originalStore.getEncryption(entry));
            }

            return newStore;
        } catch (GeneralSecurityException e) {
            throw new CryptoStoreException("Failed to convert keystore " + originalStore + " to " + ksType, e);
        } catch (IOException e) {
            throw new CryptoStoreException("Failed to convert keystore " + originalStore + " to " + ksType, e);
        }
    }
}
