
# Generate the server certificate
openssl req -nodes  -newkey rsa:2048 -keyout store.key -out store.csr \
 -subj "/C=CA/ST=Ontario/L=Toronto/O=ACME Inc/OU=pharmacy/CN=store-200"

# Sign the server certificate
openssl x509 -req -CA ../root/rootCA.crt -CAkey ../root/rootCA.key -in store.csr -out store.crt -days 365 -CAcreateserial -extfile localhost.ext

# generate the jks file for the server
openssl pkcs12 -export -out store.p12 -name "store" -inkey store.key -in store.crt
#keytool -importkeystore -srckeystore server.p12 -srcstoretype PKCS12 -destkeystore server.jks -deststoretype JKS -storepass password
