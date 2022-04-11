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

`first` will pop one object from the stack, and push one replacement object back onto the stack.

#### Errors

`first` will fail if the object at the top of the stack cannot be converted to a sequence, or if the sequence is empty.

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

#### Errors

`entry` will fail if the object at the top of the stack cannot be converted to a sequence.

-------

## To Document

- `equals`
- `not-equals`
- `filter`
- `import` 
- `info` 
- `keystore`
- `last`
- `merge`
- `pair`
- `print`
- `property`
- `read`
- `recurse`
- `remove-password`
- `seq`
- `set-password`
- `write`
