# X5 equivalents to common OpenSSL Commands

## Key Password Management


### Remove a passphrase from a Key

**_OpenSSL_**

```
openssl rsa -in encrypted.key -out open.key
```

**_X5_**

```
x5 read -p =password encrypted.key | remove-password | write open.key
```

### Add a passphrase to a Key

**_OpenSSL_**

```
openssl rsa -des3 -in open.key -out encrypted.key
```

**_X5_**

```
x5 read open.key | set-password =password | write encrypted.key
```

## View File Info

### Certificates

**_OpenSSL_**

```
openssl x509 -in certificate.crt -text -noout
```

_or_

```
openssl x509 -in certificate.crt -text -noout -fingerprint -sha256
```

**_X5_**

```
x5 read certificate.crt | info
```

### PKCS#12 Keystores

#### File Structure 
**_OpenSSL_**  
_This isn't really what we'd want, but it's the best openssl offers._

```
openssl pkcs12 -info -in -nokeys keyStore.p12
```

**_X5_**

```
x5 read keyStore.p12 | info
```

#### File Content
**_OpenSSL_**

```
openssl pkcs12 -clcerts -in src/test/resources/samples/keystore/multiple-keys.p12
```

**_X5_**

```
x5 read src/test/resources/samples/keystore/multiple-keys.p12 | each ( write - )
```

### Public Keys

#### Extract a PEM Encoded public key from a private key file

**_OpenSSL_**

```
openssl rsa -in example.key -pubout
```

**_X5_**

```
x5 read example.key | .public | write -
```

#### Determine the public modulus of private key

**_OpenSSL_**

```
openssl rsa -in example.key -noout -modulus
```

**_X5_**

```
x5 read example.key | .public | info
```

_or_	

```
x5 read example.key | .public.modulus | write -
```

### EC Private Keys

**_OpenSSL_**

```
openssl ec -text -noout -in ec1.key
```

**_X5_**

```
x5 read ec1.key | info
```

