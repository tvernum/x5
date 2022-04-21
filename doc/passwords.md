# Password handling in X5

## Command line options

The x5 entry point accepts two password related command line options

- `--password` - Specify a literal password to use everywhere where a password is needed
- `--password-file` - Specify a file the maps a password for different paths

### `--password`

This option takes a literal password value to use for all password operations.

For example, as a simple way to read a file with a known password:
```
x5 --password "my-password" 'read my.key | info'
```

### `--password-file`

This option takes the path to a file, the contents of which describe which password to use when specified files are read.

The file is read by line.
Each line includes a _path specification_, an `=`, and the _password_.
A path specification may include wildcard patterns (`*`)

For example:

**`passwords.txt`**
```
my.key=my-password
server.*=another-password
keys/*.key=yet-another-password
```

**Command line**
```
x5 --password-file passwords.txt `seq( read my.key, read server.key, read keys/another.key ) | info`
```

## Specifying passwords in commands

Some commands (e.g. `read` and `set-password`) can accept a password specification

A password specification must be in one of the following formats:

### A literal password
- `=` _literal-password_
- `literal:` _literal-password_
- `str:` _literal-password_
- `string:` _literal-password_

The password that is used is the value of _literal-password_.

For example, this command attempts to read `server.key` using the password `my-secret`

```
read -p "string:my-secret" server.key
```

This does the same, using an alternative syntax:

```
read -p "=my-secret" server.key
```
 

### A password read from a file

- `@` _file-path_
- `file:` _file-path_

The password is the content of the file at _file-path_ (with any trailing newline removed).

**Note:** The file is read verbatim, it is not interpretted in the same manner as the `--password-file` command line option. 

Example:

```
read -p "@/path/to/password.txt" server.key
```

### A password read from stdin

- `+` _prompt_
- `input:` _prompt_
- `stdin:` _prompt_
- `ask:` _prompt_

Example:

```
read -p "ask:server key password" server.key
```

### A password from an environment variable

- `${` _environment-variable_ `}`
- `$` _environment-variable_ 
- `env:` _environment-variable_
- `environment:` _environment-variable_

Example:

```
read -p "$SERVER_KEY_PASSWORD" server.key
```

