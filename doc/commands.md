# Commands and Functions Reference

## Commands

### `as` - _Perform datatype casting and conversion_

The `as` command attempts to convert the object at the top of a stack into the type specified in the command argument.

#### Arguments

`as` takes exactly one argument, the name of a X5 [data-type](types.md)

#### Example

_Convert a PEM key file into a private key (discarding any public key information)_
```
read server.key | as PrivateKey
```

#### Stack 

`as` will pop one object from the stack, and push one replacement object back onto the stack.

#### Errors

`as` will fail if the object at the top of the stack cannot be converted to the specified type.

--

### `first` - _Extract the first value from a sequence_

The `first` command pops a value from the stack, converts it to a sequence, extracts the first element from the sequence, and then pushes that to the stack

#### Arguments

`first` does not take any arguments

#### Example

_Read the first (leaf) element from a certificate chain_

```
read chain.crt | first | info
```

#### Stack 

`first` will pop one object from the stack, and push one replacement object back onto the stack.

#### Errors

`first` will fail if the object at the top of the stack cannot be converted to a sequence, or if the sequence is empty.

--

### `hex` - _Convert a number to a hexadecimal string_

The `hex` command pops a value from the stack, converts it to a number, formats it in hexadecimal (base 16) and then pushes the resulting string to the stack

#### Arguments

`hex` takes between 0 and 2 arguments
1. A separator string to print between some number of characters 
2. The number of characters to place between separators (defaults to 2)

#### Example

_Convert a number to a base 16 string_

```
256 | hex
```

_Convert a number to a base 16 string with a ":" between every 3rd character_

```
46417111758 | hex ":" "3"
```

#### Stack 

`hex` will pop one object from the stack, and push one replacement object back onto the stack.

#### Errors

`hex` will fail if the object at the top of the stack cannot be converted to a number

---

### `info` - _Print summary information about an object_

The `info` command prints information about the value at the top of the stack.

The exact content will vary depending on the [type](types.md) of the object, but will include all the properties of the object (see the `property` command).

#### Arguments

`info` does not take any arguments

#### Example

_Display information about a certificate_

```
read server.crt | info
```

#### Stack

`info` does not affect the stack

#### Errors

`info` will fail if the stack is empty

--

### `last` - _Extract the last value from a sequence_

The `last` command pops a value from the stack, converts it to a sequence, extracts the last element from the sequence, and then pushes that to the stack

#### Arguments

`last` does not take any arguments

#### Example

_Read the last (CA) element from a certificate chain_

```
read chain.crt | last | info
```

#### Stack

`last` will pop one object from the stack, and push one replacement object back onto the stack.

#### Errors

`last` will fail if the object at the top of the stack cannot be converted to a sequence, or if the sequence is empty.

--

### `property` - _Extract a property from an object_

The `property` command pops an object from the stack, extracts a property from it, and then pushes the resulting object onto the stack.

#### Arguments

`property` takes one or more arguments. Each argument is a property to extract. The first property is extracted from the object on the top of the stack. Each subsequent property is extracted from the previous property value.

#### Operators

- The `.` operator can be used as an alias for the `property` command

#### Example

_Read an RSA key file and extract the private key_

```
read rsa.key | property private | info
```

_Read a certificate file and extract the subject and issuer_

```
read server.crt | seq ( .issuer, .subject ) | print
```

_Read a certificate file and extract the public key algorithm_

```
read server.crt | property key algorithm | print
```

_As above, using the `.` syntax_

```
read server.crt | .key.algorithm | print
```

#### Stack

`property` will pop one object from the stack, and push one replacement object back onto the stack.

#### Errors

`property` will fail if the stack is empty, or if the object on top of that stack does not contain the specified property.

--

### `read` - _Read from a file_

The `read` command reads from a file and pushes the resulting object onto the stack.

The [type](types.md) of object that is pushed onto the stack varies based on the type of file that is read.


#### Options

`read` has one optional argument:

- `-p`, `--password` - Apply the specified [password](passwords.md) when reading from the specified file

#### Arguments

`read` takes exactly one argument, the path to a file

#### Example

_Read a PEM file and print summary information to standard out_

```
read chain.crt | info
```
--

### `set-password` - _Change the password on an encrypted object_

The `set-password` changes the password on the object at the top of the stack.

Strictly speaking it pops the existing object from the stack, creates a cloned object with a new password,
and pushes that new object to the stack.

#### Options

`set-password` has one optional argument:

- `-r`, `--recurse` - If the target object is a keystore, also change the password for any key entries
 (provided those entries previously used the same password as the keystore itself).
 
#### Arguments

`set-password` takes exactly one argument, the [password](passwords.md) to apply to the object

#### Example

_Change the password on a PKCS#12 keystore_

```
read -p "=old-password" keystore.p12 | set-password -r "=new-password" | write updated-keystore.p12
```

#### Stack

`set-password` will pop one object from the stack, and push one replacement object back on the stack.

#### Errors

`set-password` will fail if the object at the top of the stack is not an encryptable object.

--

### `to` - _Convert file format or syntax_

The `to` command attempts to convert the object at the top of a stack into a new file format or encoding sytax, as specified in the command argument.


#### Arguments

`to` takes exactly one argument, the format/syntax into which the object should be converted.

#### Example

_Convert a JKS keystore to PKCS#12_

```
read keystore.jks | to pkcs12 | write keystore.p12 
```

#### Stack

`to` will pop one object from the stack, and push one replacement object back onto the stack.

#### Errors

`to` will fail if the object at the top of the stack cannot be converted to the specified format.

## Functions

### `each` - _Iterate over elements in a sequence_

The `each` function iterates over the object at the top of the stack and applies a set of commands to each element.

A new sequence is pushed onto the stack, where each element is the result from each command (the top of the stack after executing the command argument).

#### Arguments

`each` takes exactly one argument - an expression to evaluate for each argument in the target sequence 

#### Example
_Print each element in a certificate chain_

```
read certificate-chain.crt | each ( info )
```

#### Stack

`each` will pop one object from the stack, and push one replacement object back onto the stack.

#### Errors

`each` will fail if the object at the top of the stack cannot be converted to a sequence.

--

### `entry`- _Construct a new keystore entry_

The `entry` function creates a new object of [type `StoreEntry`](types.md), from the provided arguments

#### Arguments

`entry` takes exactly two arguments

1. A [string](types.md), the name of the entry
2. A [cryptographic](types.md) object, the value for the entry 

#### Example
```
entry( "ca", read certificate_authority.crt )
```

#### Stack

`entry` will push one object onto the stack (the StoreEntry)

--

### `keystore`- _Construct a new keystore_ 

The `keystore` function creates a new object of [type `Store`](types.md), from the provided arguments

#### Arguments

`keystore` takes any number of arguments. Each argument must be a [`StoreEntry`](types.md), or a [`Cryptographic Object`](types.md).
If the argument is a `Cryptographic Object` a store entry is contructed dynamically, with a name derived from the cryptogaphic object.

#### Example
```
keystore( entry( "ca", read certificate_authority.crt ), pair( read server.crt, read server.key ) )
```

#### Stack

`keystore` will push one object onto the stack (the Store)

--

### `pair`- _Construct a new key-pair_

The `pair` function creates a new object of [type `KeyPair`](types.md), from the provided arguments

#### Arguments

`pair` takes exactly two arguments

- A [`PrivateCredential`](types.md)
- A [`PublicCredential`](types.md)

These 2 arguments may be in either order 

#### Example
```
pair( read ca.crt , read ca.key )
```

#### Stack

`pair` will push one object onto the stack (the KeyPair)

--

### `seq`- _Construct a new sequence_

The `seq` function creates a new object of [type `Sequence`](types.md), from the provided arguments

#### Arguments

`seq` takes 0 or more arguments

#### Example
```
seq( "a", "b", "c" )
```

```
seq( read ca.crt , read intermediate.crt, read leaf.crt ) | as CertificateChain
```

#### Stack

`seq` will push one object onto the stack (the Sequence)

-------

## To Document

- `equals`
- `not-equals`
- `filter`
- `import` 
- `merge`
- `print`
- `recurse`
- `remove-password`
- `write`
