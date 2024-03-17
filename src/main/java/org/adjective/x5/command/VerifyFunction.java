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

import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.adjective.x5.cli.CommandLine;
import org.adjective.x5.cli.CommandRunner;
import org.adjective.x5.command.util.SimpleOptions;
import org.adjective.x5.exception.BadArgumentException;
import org.adjective.x5.exception.CommandExecutionException;
import org.adjective.x5.exception.InvalidTargetException;
import org.adjective.x5.exception.LibraryException;
import org.adjective.x5.exception.UncheckedException;
import org.adjective.x5.exception.UnexpectedTypeException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.Certificate;
import org.adjective.x5.types.CertificateChain;
import org.adjective.x5.types.CryptoStore;
import org.adjective.x5.types.FailureResult;
import org.adjective.x5.types.ObjectSequence;
import org.adjective.x5.types.SuccessResult;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5Result;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.types.crypto.JCAConversion;
import org.adjective.x5.types.value.Password;
import org.adjective.x5.util.ArrayBuilder;
import org.adjective.x5.util.CheckedBiConsumer;

import joptsimple.OptionSpec;

import org.adjective.x5.util.CheckedBiFunction;
import org.adjective.x5.util.Values;

public class VerifyFunction extends EvaluatedFunction<X5Object> implements CommandLineFunction {

    private final class Options extends SimpleOptions {
        final OptionSpec<Void> client;
        final OptionSpec<Void> server;
        final OptionSpec<String> algorithm;
        final OptionSpec<String> provider;
        final OptionSpec<Void> sequence;

        public Options() {
            super(false);
            this.client = declareValuelessOption("client");
            this.server = declareValuelessOption("server");
            this.algorithm = declareStringOption("algorithm", "algo");
            this.provider = declareStringOption("provider", "prov");
            this.sequence = declareValuelessOption("sequence", "seq");
        }
    }

    private final Options options;

    public VerifyFunction() {
        options = new Options();
    }

    @Override
    public String name() {
        return "verify";
    }

    @Override
    protected X5Object evaluateFunction(CommandRunner runner, List<String> cliOptions, List<CommandLine> argumentExpressions)
        throws X5Exception {
        final SimpleOptions.ParsedOptions opts = this.options.parse(cliOptions);

        if (opts.has(options.client) && opts.has(options.server)) {
            throw new BadArgumentException(
                "Cannot specify both " + options.client + " and " + options.server + " to function " + name(),
                this
            );
        }

        requireMinimumArgumentCount(1, argumentExpressions);

        final CryptoStore store = popStack(runner.getValues(), X5Type.STORE);
        final String algorithm = opts.get(this.options.algorithm, "PKIX");
        final Optional<String> provider = opts.maybe(this.options.provider);
        final TrustManagerFactory trustManagerFactory;
        try {
            if (provider.isPresent()) {
                trustManagerFactory = TrustManagerFactory.getInstance(algorithm, provider.get());
            } else {
                trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
            }

            trustManagerFactory.init(JCAConversion.store(store, new Password("", getSource())));
        } catch (GeneralSecurityException e) {
            throw new LibraryException(
                "Cannot load trust manager for algorithm " + algorithm + " and provider " + provider.orElse(null),
                e
            );
        }
        final List<X509TrustManager> trustManagers = Arrays.stream(trustManagerFactory.getTrustManagers())
            .filter(X509TrustManager.class::isInstance)
            .map(X509TrustManager.class::cast)
            .collect(Collectors.toUnmodifiableList());

        if (trustManagers.isEmpty()) {
            throw new InvalidTargetException(store, "Cannot construct any X509 trust managers from ");
        }

        CheckedBiFunction<X509TrustManager, CertificateChain, X5Result, X5Exception> verify = (trustManager, chain) -> {
            java.security.cert.X509Certificate[] array = toJava(chain);
            String authType = authType(chain, array);
            try {
                if (opts.has(options.client)) {
                    trustManager.checkClientTrusted(array, authType);
                } else {
                    trustManager.checkServerTrusted(array, authType);
                }
                return new SuccessResult(getSource());
            } catch (CertificateException e) {
                return new FailureResult(e.getMessage(), getSource());
            }
        };

        final List<X5Result> seq = opts.has(options.sequence) ? new ArrayList<>() : null;
        X5Result result = null;
        for (int i = 0; i < argumentExpressions.size(); i++) {
            CommandLine arg = argumentExpressions.get(i);
            final X5Object eval = eval(arg, runner);
            var chain = asType(X5Type.CERTIFICATE_CHAIN, eval, i);
            result = verify(verify, trustManagers, chain);
            if (seq != null) {
                seq.add(result);
            } else if (result.isError()) {
                return result;
            }
        }

        return seq == null ? result : new ObjectSequence(seq, getSource());
    }

    private X5Result verify(
        CheckedBiFunction<X509TrustManager, CertificateChain, X5Result, X5Exception> verify,
        List<X509TrustManager> trustManagers,
        CertificateChain chain
    ) throws X5Exception {
        X5Result result = null;
        for (var tm : trustManagers) {
            result = verify.apply(tm, chain);
            if (result.isError()) {
                return result;
            }
        }
        return result;
    }

    private java.security.cert.X509Certificate[] toJava(CertificateChain chain) throws X5Exception {
        var c = JCAConversion.chain(chain);
        var x = new X509Certificate[c.length];
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof X509Certificate) {
                x[i] = (X509Certificate) c[i];
            } else {
                throw new UnexpectedTypeException(
                    chain.certificates().get(i),
                    "Certificate #" + (i + 1) + " of chain " + chain.description() + " is not an X.509 certificate"
                );
            }
        }
        return x;
    }

    private static final Set<String> VALID_AUTH_TYPES = Set.of("RSA", "DSA", "EC");

    private String authType(CertificateChain chain, java.security.cert.X509Certificate[] java) throws X5Exception {
        final String algorithm = java[0].getPublicKey().getAlgorithm();
        if (VALID_AUTH_TYPES.contains(algorithm)) {
            return algorithm;
        } else {
            throw new CommandExecutionException("Unrecognised certificate algorithm " + algorithm + " in " + chain.leaf());
        }
    }
}
