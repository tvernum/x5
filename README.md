# X5

`x5` is a tool for working with the sorts of cryptographic objects and files that are commonly used for TLS/SSL.

It provides a command line interface for reading certificates, keys & keystores and processing them in various ways.
The functionality overlaps with some of the ways that the `openssl` commandline tool might be used.

## Obtaining

Check the [releases page](https://github.com/tvernum/x5/releases) on GitHub

## Usage

Read a PEM file (of any type) and write details to the console:
```
x5 'read file.pem | info'
```

Read a PKCS#12 keystore and write full details of each pair:
```
x5 'read keystore.p12 | each ( .value | each ( info ) )'
```

The above command requires some explanation.
The design of `x5` is that there is a single command (`x5`) with a rich expression language that allows the user to dig into the contents of any cryptographic object and treat them in consistent ways.
Thus, there is no "keystore" or "pkcs12" command, there is just "read" and process.

The command above says:
1. `read` the `keystore.p12` file (and sniff its content type)
2. pipe (`|`) the result of reading that file into the `each` function. `each` can be applied to anything that is a sequence, and a keystore is a sequence of "entries" (a name+value pair), so the argument to `each` will be a "store entry"
3. For `each` store entry, we get the `value` property (using the `.` operator). In this case the value is a key-pair (a certificate and private key).
4. For `each` element in the key pair (that is the private key and the public certifiacte) we print out the object's `info`


