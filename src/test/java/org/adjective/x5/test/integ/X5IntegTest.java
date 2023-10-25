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

package org.adjective.x5.test.integ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.adjective.x5.TestRunner;
import org.adjective.x5.command.Environment;
import org.adjective.x5.io.BaseFileSystem;
import org.adjective.x5.io.SpecialFile;
import org.adjective.x5.io.StdIO;
import org.adjective.x5.io.password.FilePasswordSupplier;
import org.adjective.x5.io.password.PasswordSupplier;
import org.adjective.x5.test.util.ShadowedFile;
import org.adjective.x5.types.X5File;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class X5IntegTest {

    private static Path testSuiteDirectory = findTestSuiteDirectory();
    private static Path samplesDirectory = findSamplesDirectory();
    private static Path outputDirectory = createOutputDirectory();

    private static Path findTestSuiteDirectory() {
        return getParentDirectory("/test-suite/README.md");
    }

    private static Path findSamplesDirectory() {
        return getParentDirectory("/samples/passwords.txt");
    }

    private static Path createOutputDirectory() {
        try {
            final Path dirs = Files.createTempDirectory(X5IntegTest.class.getSimpleName());
            wipe(dirs);
            return dirs;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void wipe(Path path) throws IOException {
        Files.list(path).forEach(p -> {
            try {
                if (Files.isDirectory(p)) {
                    wipe(p);
                } else {
                    Files.deleteIfExists(p);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });

    }

    private static Path getParentDirectory(String fileName) {
        final URL readme = X5IntegTest.class.getResource(fileName);
        if (readme == null) {
            return null;
        }
        try {
            return Paths.get(readme.toURI()).getParent();
        } catch (URISyntaxException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    public static List<String> testSuite() throws IOException {
        if (testSuiteDirectory == null) {
            throw new IllegalStateException("Cannot find test suite directory");
        }
        if (samplesDirectory == null) {
            throw new IllegalStateException("Cannot find sample file directory");
        }
        if (outputDirectory == null) {
            throw new IllegalStateException("Could not create output directory");
        }
        return Files.list(testSuiteDirectory)
            .filter(Files::isDirectory)
            .map(Path::getFileName)
            .map(Path::toString)
            .collect(Collectors.toList());
    }

    @ParameterizedTest
    @MethodSource("testSuite")
    public void test(String suiteName) throws Exception {
        var dir = testSuiteDirectory.resolve(suiteName);
        assertThat(dir).isDirectory();
        var command = dir.resolve("command.txt");
        assertThat(command).isRegularFile();
        var output = dir.resolve("output.txt");
        assertThat(output).isRegularFile();

        var stdinPath = dir.resolve("stdin.txt");
        var suitePasswords = dir.resolve("password.txt");
        var sharedPasswords = samplesDirectory.resolve("passwords.txt");
        final List<Path> passwordFiles = Stream.of(suitePasswords, sharedPasswords).filter(Files::exists).collect(Collectors.toList());

        final Environment environment = new Environment();
        final FilePasswordSupplier passwordSupplier = new FilePasswordSupplier(environment, passwordFiles);

        final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        final var stdio = buildStdio(stdinPath, stdout);
        final TestFileSystem fileSystem = new TestFileSystem(passwordSupplier, stdio);

        var commandText = Files.readAllLines(command);
        final Optional<Exception> result = new TestRunner().run(commandText, passwordSupplier, stdio, fileSystem, environment);
        result.ifPresent(e -> { throw new RuntimeException("Test Failed", e); });

        var expectedOutput = Files.readAllLines(output);
        assertThat(getOutputLines(stdout)).containsExactlyElementsOf(expectedOutput);
    }

    public List<String> getOutputLines(ByteArrayOutputStream out) {
        final byte[] bytes = out.toByteArray();
        final String output = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes)).toString();
        return List.of(output.split("\n"));
    }

    private StdIO buildStdio(Path inputSpecPath, ByteArrayOutputStream bytesOut) throws IOException {
        var output = new PrintStream(bytesOut);
        if (Files.exists(inputSpecPath) == false) {
            return new StdIO(output, InputStream.nullInputStream());
        }

        final InputStream input;
        final String inputSpec = Files.readString(inputSpecPath);
        if (inputSpec.length() == 0) {
            throw new IOException("File " + inputSpecPath.toAbsolutePath() + " is empty");
        }

        switch (inputSpec.charAt(0)) {
            case '@': {
                var stdinPath = samplesDirectory.resolve(Paths.get(inputSpec.substring(1).trim()));
                if (Files.exists(stdinPath)) {
                    input = Files.newInputStream(stdinPath, StandardOpenOption.READ);
                } else {
                    throw new FileNotFoundException(stdinPath.toAbsolutePath().toString());
                }
                break;
            }
            case '=': {
                input = new ByteArrayInputStream(inputSpec.substring(1).getBytes(StandardCharsets.UTF_8));
                break;
            }
            default: {
                throw new IOException("Invalid marker char '" + inputSpec.charAt(0) + "' in file " + inputSpecPath.toAbsolutePath());
            }
        }
        return new StdIO(output, input);
    }

    private static class TestFileSystem extends BaseFileSystem {

        public TestFileSystem(PasswordSupplier passwordSupplier, StdIO stdio) throws IOException {
            super(passwordSupplier, stdio);
        }

        @Override
        public Path resolve(String path) {
            return Path.of(path);
        }

        @Override
        protected X5File readPath(Path requestedPath, PasswordSupplier passwords) throws FileNotFoundException {
            Path resolvedPath = samplesDirectory.resolve(requestedPath);
            if (Files.exists(resolvedPath) == false) {
                final Path outputPath = outputDirectory.resolve(requestedPath);
                if (Files.exists(outputPath)) {
                    resolvedPath = outputPath;
                }
            }
            checkReadable(resolvedPath);
            return new ShadowedFile(resolvedPath, requestedPath, passwords);
        }

        @Override
        public OutputStream writeTo(Path requestedPath, boolean overwrite) throws IOException {
            final Path resolvedPath = outputDirectory.resolve(requestedPath);
            final Path parent = resolvedPath.getParent();
            if (Files.exists(parent) == false) {
                Files.createDirectory(parent);
            }
            return super.writeTo(resolvedPath, overwrite);
        }
    }

}
