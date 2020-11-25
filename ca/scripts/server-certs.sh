
# Generate the root CA certificate
openssl req -x509 -sha256 -days 3650 -newkey rsa:2048 -keyout rootCA.key -out rootCA.crt \
 -subj "/C=CA/ST=Ontario/L=Toronto/O=Test Certificate Authority/OU=dev team/CN=example.com"

# Generate the server certificate
openssl req -nodes  -newkey rsa:2048 -keyout server.key -out server.csr \
 -subj "/C=CA/ST=Ontario/L=Toronto/O=Example Inc/OU=Demos Department/CN=greeting server"

# Sign the server certificate
openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in server.csr -out server.crt -days 365 -CAcreateserial -extfile scripts/localhost.ext

# generate the jks file for the server
openssl pkcs12 -export -out server.p12 -name "server" -inkey server.key -in server.crt
#keytool -importkeystore -srckeystore server.p12 -srcstoretype PKCS12 -destkeystore server.jks -deststoretype JKS -storepass password
