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

import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.adjective.x5.cli.CommandLine;
import org.adjective.x5.cli.CommandRunner;
import org.adjective.x5.exception.CryptoStoreException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.CertificateChain;
import org.adjective.x5.types.CryptoStore;
import org.adjective.x5.types.CryptoValue;
import org.adjective.x5.types.KeyPair;
import org.adjective.x5.types.Sequence;
import org.adjective.x5.types.StoreEntry;
import org.adjective.x5.types.X509Certificate;
import org.adjective.x5.types.X5Key;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.types.crypto.BasicStoreEntry;
import org.adjective.x5.types.crypto.SimpleKeyStore;
import org.adjective.x5.util.Values;

public class KeystoreFunction extends AbstractFunction implements CommandLineFunction {

    @Override
    public String name() {
        return "keystore";
    }

    @Override
    public void apply(CommandRunner runner, List<String> options, List<CommandLine> args) throws X5Exception {
        List<X5Object> list = new ArrayList<>();
        for (CommandLine a : args) {
            X5Object eval = eval(a, runner);
            list.add(eval);
        }
        final List<X5Object> objects = Collections.unmodifiableList(list);
        final X5StreamInfo source;
        if (objects.size() == 1) {
            source = objects.get(0).getSource().withDescriptionPrefix("keystore from");
        } else {
            source = Values.source(
                "keystore("
                    + objects.stream().map(X5Object::getSource).map(X5StreamInfo::getSourceDescription).collect(Collectors.joining(","))
                    + ")"
            );
        }
        try {
            var keyStore = new SimpleKeyStore(source);

            for (int i = 0; i < objects.size(); i++) {
                var entry = asEntry(objects.get(i), i, keyStore);
                keyStore.addEntry(entry, Optional.empty());
            }
            runner.getValues().push(keyStore);
        } catch (KeyStoreException e) {
            throw new CryptoStoreException("Failed to create keystore", e);
        }
    }

    private StoreEntry asEntry(X5Object argument, int argIndex, CryptoStore keystore) throws X5Exception, KeyStoreException {
        final Optional<StoreEntry> entry = argument.as(StoreEntry.class);
        if (entry.isPresent()) {
            return entry.get();
        }
        var cv = argument.as(CryptoValue.class);
        if (cv.isPresent()) {
            String name = guessName(keystore, cv.get());
            return new BasicStoreEntry(Optional.empty(), name, cv.get());
        }
        throw super.badArgumentType(X5Type.STORE_ENTRY, argIndex);
    }

    private String guessName(CryptoStore store, CryptoValue obj) throws X5Exception {
        if (obj instanceof X509Certificate) {
            var cert = (X509Certificate) obj;
            return unique(
                cert.subject()
                    .leaf()
                    .getAttributes()
                    .stream()
                    .map(ava -> ava.getAttributeValue().toLowerCase(Locale.ROOT).replaceAll("\\s+", "-"))
                    .collect(Collectors.joining("_")),
                store
            );
        }
        if (obj instanceof CertificateChain) {
            var chain = (CertificateChain) obj;
            return guessName(store, chain.certificates().get(0));
        }
        if (obj instanceof KeyPair) {
            var pair = (KeyPair) obj;
            return guessName(store, pair.publicCredential());
        }
        if (obj instanceof Sequence) {
            var seq = (Sequence) obj;
            final Iterator<? extends X5Object> iterator = seq.items().iterator();
            if (iterator.hasNext()) {
                final X5Object next = iterator.next();
                if (next instanceof CryptoValue) {
                    return guessName(store, (CryptoValue) next);
                }
            }
        }
        if (obj instanceof X5Key) {
            var key = (X5Key) obj;
            return key.getKeyType().toLowerCase(Locale.ROOT);
        }
        return obj.getTypeName();
    }

    private String unique(String name, CryptoStore store) throws X5Exception {
        if (store.findEntry(name).isEmpty()) {
            return name;
        }
        int index = 1;
        for (;;) {
            var alias = name + "-" + index;
            if (store.findEntry(alias).isEmpty()) {
                return alias;
            }
        }
    }
}
