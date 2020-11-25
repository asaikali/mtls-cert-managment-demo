
# Generate the root CA certificate
openssl req -x509 -sha256 -days 3650 -newkey rsa:2048 -keyout rootCA.key -out rootCA.crt \
 -subj "/C=CA/ST=Ontario/L=Toronto/O=Test Certificate Authority/OU=dev team/CN=example.com"
