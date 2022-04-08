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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.adjective.x5.TestRunner;
import org.adjective.x5.command.Environment;
import org.adjective.x5.io.password.FilePasswordSupplier;
import org.adjective.x5.io.password.PasswordSpec;
import org.adjective.x5.io.password.PasswordSupplier;
import org.adjective.x5.types.X5File;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.adjective.x5.io.BaseFileSystem;
import org.adjective.x5.test.util.ShadowedFile;

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
            return Files.createTempDirectory(X5IntegTest.class.getSimpleName());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

        final Environment environment = new Environment();
        final FilePasswordSupplier passwordSupplier = new FilePasswordSupplier(
            environment,
            Set.of(samplesDirectory.resolve("passwords.txt"))
        );
        final TestFileSystem fileSystem = new TestFileSystem(passwordSupplier);

        var commandText = Files.readAllLines(command);
        final TestRunner.Result result = new TestRunner().run(commandText, passwordSupplier, fileSystem, environment);
        result.exception().ifPresent(e -> { throw new RuntimeException("Test Failed", e); });

        var expectedOutput = Files.readAllLines(output);
        assertThat(result.getOutputLines()).containsExactlyElementsOf(expectedOutput);
    }

    private static class TestFileSystem extends BaseFileSystem {

        public TestFileSystem(PasswordSupplier passwordSupplier) throws IOException {
            super(passwordSupplier);
        }

        @Override
        public Path resolve(String path) {
            return Path.of(path);
        }

        @Override
        public X5File read(Path requestedPath, PasswordSpec password) throws FileNotFoundException {
            final Path resolvedPath = samplesDirectory.resolve(requestedPath);
            checkReadable(resolvedPath);
            final PasswordSupplier passwords = resolvePasswordSupplier(password);
            return new ShadowedFile(resolvedPath, requestedPath, passwords);
        }

        @Override
        public OutputStream writeTo(Path requestedPath, boolean overwrite) throws IOException {
            Path resolvedPath = outputDirectory.resolve(requestedPath);
            return super.writeTo(resolvedPath, overwrite);
        }
    }

}
