
# Generate the server certificate
openssl req -nodes  -newkey rsa:2048 -keyout server.key -out server.csr \
 -subj "/C=CA/ST=Ontario/L=Toronto/O=Example Inc/OU=Demos Department/CN=greeting server"

# Sign the server certificate
openssl x509 -req -CA ../root/rootCA.crt -CAkey ../root/rootCA.key -in server.csr -out server.crt -days 365 -CAcreateserial -extfile localhost.ext

# generate the pckcs12 file for the server
openssl pkcs12 -export -out server.p12 -name "server" -inkey server.key -in server.crt

# java can work directly with pcks12 files no need to create a jks file is deprecated
#keytool -importkeystore -srckeystore server.p12 -srcstoretype PKCS12 -destkeystore server.jks -deststoretype JKS -storepass password
