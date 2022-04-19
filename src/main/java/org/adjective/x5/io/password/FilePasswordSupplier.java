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

package org.adjective.x5.io.password;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.adjective.x5.command.Environment;
import org.adjective.x5.io.Debug;
import org.adjective.x5.types.FileType;
import org.adjective.x5.types.PathInfo;
import org.adjective.x5.types.value.Password;

public class FilePasswordSupplier extends BasePasswordSupplier {
    private final Map<String, Password> passwords;
    private final String source;

    public FilePasswordSupplier(Environment environment, Iterable<Path> files) throws IOException {
        super(environment);
        this.passwords = new LinkedHashMap<>();
        var str = new StringBuilder();
        for (Path path : files) {
            read(path);
            if (str.length() > 0) {
                str.append(',');
            }
            str.append(path);
        }
        this.source = str.toString();
    }

    public FilePasswordSupplier(Environment environment, List<String> fileNames) throws IOException {
        this(environment, toPaths(fileNames));
    }

    private static Iterable<Path> toPaths(List<String> fileNames) {
        return fileNames.stream().map(Paths::get).collect(Collectors.toList());
    }

    private void read(Path path) throws IOException {
        int lineNum = 0;
        for (String line : Files.readAllLines(path)) {
            lineNum++;
            int eq = line.indexOf('=');
            if (eq == -1) {
                continue;
            }
            PathInfo info = new PathInfo(path, "Line " + lineNum, FileType.UNSPECIFIED, Optional.empty());
            String filePattern = line.substring(0, eq);
            Password password = new Password(line.substring(eq + 1).toCharArray(), info);
            passwords.put(filePattern, password);
        }
    }

    @Override
    public Password get(Path path) {
        return lookup(path.toString()).orElseThrow(() -> new IllegalArgumentException("No password configured for path " + path));
    }

    @Override
    protected Password input(String text) {
        String lookupValue = "+" + text;
        return lookup(lookupValue).orElseThrow(() -> new IllegalArgumentException("No password configured for value " + lookupValue));
    }

    private Optional<Password> lookup(String lookupValue) {
        if (passwords.containsKey(lookupValue)) {
            Debug.printf("[%s] Lookup key '%s' exists", this, lookupValue);
            return Optional.of(passwords.get(lookupValue));
        }
        for (String key : passwords.keySet()) {
            if (match(key, lookupValue)) {
                Debug.printf("[%s] Lookup %s matches password entry '%s'", this, lookupValue, key);
                return Optional.of(passwords.get(key));
            }
        }
        Debug.printf("[%s] No value for lookup '%s'", this, lookupValue);
        return Optional.empty();
    }

    private boolean match(String configKey, String lookup) {
        if (configKey.indexOf('*') == -1) {
            return configKey.equals(lookup);
        }
        if (configKey.startsWith("*") && configKey.lastIndexOf('*') == 0) {
            return lookup.endsWith(configKey.substring(1));
        }
        if (configKey.startsWith("**/") && configKey.lastIndexOf('*') == 1) {
            return lookup.equals(configKey.substring(3)) || lookup.endsWith(configKey.substring(2));
        }
        if (configKey.endsWith("*") && configKey.indexOf('*') == configKey.length() - 1) {
            return lookup.startsWith(configKey.substring(0, configKey.length() - 1));
        }
        if (configKey.startsWith("*") && configKey.endsWith("*") && configKey.substring(1, configKey.length() - 1).indexOf('*') == -1) {
            return lookup.contains(configKey.substring(1, configKey.length() - 1));
        }
        if (configKey.startsWith("**/") && configKey.endsWith("*") && configKey.substring(3, configKey.length() - 1).indexOf('*') == -1) {
            return lookup.startsWith(configKey.substring(3, configKey.length() - 1))
                || lookup.contains(configKey.substring(2, configKey.length() - 1));
        }
        throw new IllegalStateException(
            "Bad file pattern " + configKey + " at " + passwords.get(configKey).getSource().getSourceDescription()
        );
    }

    @Override
    public String toString() {
        return "password-file " + source;
    }
}
