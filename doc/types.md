# X5 Types Reference

## Introduction

Every value in x5 has a _type_.

Some commands and functions can only be used on certain types of objects. Others commands or functions produce specific types of objects.

For example the `seq` function always produces a `Sequence` object, and the `pair` function always produces a `KeyPair` object.

The `hex` command can only be used on a `Number` object and always produces a `String` object.

The `read` command produces different types of objects depending on the contents of the file that is read.

### Converting between types

Many objects can be converted to different types.

For example, the `CertificateChain` type can be converted to a `Sequence` of `Certificate` objects, and a `KeyPair` object can be converted to either its `PrivateCredential` or `PublicCredential`.

Most commands and functions will automatically convert an object to the required type (if possible). For cases where the automatic conversion doesn't produce the correct result the `as` command can be used to explicitly convert a type.

## Types

### Value Types

#### Value

A common base type for all value types.

*Child Types*: `Algorithm`, `ASN.1`, `Boolean`, `Date`, `Distinguished Name`, `Null`, `Number`, `OID`, `Password`, `String`

#### String

A `String` is a sequence of characters.

*Parent Type*: `Value`

#### Number

A `Number` is an numeric value.

*Parent Type*: `Value`

#### Boolean

A `Boolean` is a true or false value.

*Parent Type*: `Value`

#### Date

A `Date` represents a point in time (including a Time Zone)

*Parent Type*: `Value`

#### Password

A `Password` is a string value that specifically represents a password or passphrase.
A distinction is made between a `String` and a `Password` so that passwords are not printed to the console.

*Parent Type*: `Value`

#### Distinguished Name

A `Distinguished Name` is a string value that represents a _DN_ in the X.500 standard.

*Parent Type*: `Value`
 
#### OID

An `OID` represents an [_Object Identifier_](https://en.wikipedia.org/wiki/Object_identifier) in the X.660 standard.

*Parent Type*: `Value`

#### Algorithm

An `Algorithm` is a form of OID that specifically stores a cryptographic algorithm.

*Parent Type*: `Value`

#### ASN.1

An `ASN.1` object is a sequence of bytes that store an [ASN.1](https://en.wikipedia.org/wiki/ASN.1) data structure.

*Parent Type*: `Value`

#### Null

A special value type to represent _null_ values, where no other type information is available. 

*Parent Type*: `Value`

### Cryptographic Types

#### Cryptographic Object

A common type for all cryptographic values

*Child Types*: `Key Pair`, `Private Credential`, `Public Credential`

#### Public Credential

A credential that is designed to be shared publicly.

*Parent Type*: `Cryptographic Object`

*Child Types*: `Certificate`, `Certificate Chain`, `Public Key`

#### Certificate

A Certificate, typically (but not necessarily) in X.509 format

*Parent Type*: `Public Credential`

#### Certificate Chain

A sequence of `Certificate` objects, ordered from leaf certificate to issuer.

*Parent Types*: `Public Credential`, `Sequence`

#### Public Key

A public key, possibly (but not necessarily) an RSA or EC public key.

*Parent Types*: `Public Credential`

#### Private Credential

A credential that must be kept confidential.

*Parent Type*: `Cryptographic Object`

*Child Types*: `Private Key`, `Secret Key`

#### Private Key

A private key, possibly (but not necessarily) an RSA or EC key.

*Parent Types*: `Private Credential`

#### Secret Key

A secret (symmetric) key, possibly (but not necessarily) an AES key.

*Parent Types*: `Private Credential`

#### Key Pair

A pair of credentials - one `Private Credential` and one `Public Credential`.

*Parent Type*: `Cryptographic Object`

### Container Types

#### Store

A store for multiple `Cryptographic Object` entries. Each object is stored with a name (a `String`) and a value. 

*Parent Type*: `Sequence` (of `Store Entry`)

#### Store Entry

An entry in a `Store`. It consists of a name (a `String`) and a value (a `Cryptographic Object`).

#### Sequence  

A list of objects. The elements are not guaranteed to be of a single type. 

#### Record

A collection of named objects.

### Other Types

#### Result

A success/failure result. A Result is either a success (OK) or a failure with a message (Err)
