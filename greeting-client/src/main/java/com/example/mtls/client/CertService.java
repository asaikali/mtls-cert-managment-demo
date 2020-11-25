package com.example.mtls.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Optional;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.SSLContext;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
class CertService {

  private final CertRepository certRepository;
  private final KeyStore rootCA;

  public CertService(CertRepository certRepository) {
    this.certRepository = certRepository;
    try {
      this.rootCA = this.loadRootCA( new ClassPathResource("rootCA.jks").getInputStream(),"password");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  public Optional<RestTemplate> getStoreRestTemplate(String storeId,
      String password) {

    return this.getCertKeyStore(storeId, password)
        .map(keyStore -> buildRestTemplate(password, keyStore));
  }

  private RestTemplate buildRestTemplate(String password, KeyStore keyStore) {
    SSLContext sslContext = buildSSLContext(keyStore, rootCA, password.toCharArray());
    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext,
        NoopHostnameVerifier.INSTANCE);
    HttpClient httpClient = HttpClients.custom()
        .setSSLSocketFactory(csf)
        .build();
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
        httpClient);
    return new RestTemplate(requestFactory);
  }

  private SSLContext buildSSLContext(KeyStore keyStore, KeyStore trustStore, char[] keyPassword) {
    try {
      return SSLContexts.custom()
          .loadTrustMaterial(trustStore, TrustSelfSignedStrategy.INSTANCE)
          .loadKeyMaterial(keyStore, keyPassword)
          .build();
    } catch (NoSuchAlgorithmException
        | KeyManagementException | KeyStoreException | UnrecoverableKeyException e) {
      throw new RuntimeException("Cloud not setup SSLContext", e);
    }
  }

  private Optional<KeyStore> getCertKeyStore(String storeId, String password) {
    for (CertEntity certEntity : this.certRepository.findStoreCerts(storeId)) {
      Instant now = Instant.now();
      if (now.isAfter(certEntity.getNotBefore()) && now.isBefore(certEntity.getNotAfter())) {
        byte[] bytes = Base64.getUrlDecoder().decode(certEntity.getData());
        return Optional.of(loadPkcsKeyStore(bytes, password));
      }
    }
    return Optional.empty();
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void addCert(byte[] keyStoreData, String password) {
    try {
      KeyStore keyStore = this.loadPkcsKeyStore(keyStoreData, password);
      Enumeration<String> aliases = keyStore.aliases();
      while (aliases.hasMoreElements()) {
        String alias = aliases.nextElement();
        X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);

        CertEntity certEntity = new CertEntity();
        certEntity.setAlias(alias);
        certEntity.setStore(getCommonName(x509Certificate));
        certEntity.setData(Base64.getUrlEncoder().encodeToString(keyStoreData));
        certEntity.setNotAfter(x509Certificate.getNotAfter().toInstant());
        certEntity.setNotBefore(x509Certificate.getNotBefore().toInstant());

        certRepository.save(certEntity);
      }
    } catch (KeyStoreException e) {
      throw new RuntimeException(e);
    }
  }

  private String getCommonName(X509Certificate x509Certificate) {

    try {
      String dn = x509Certificate.getSubjectX500Principal().getName();
      LdapName ldapDN = new LdapName(dn);
      for (Rdn rdn : ldapDN.getRdns()) {
        if (rdn.getType().equalsIgnoreCase("cn")) {
          return rdn.getValue().toString();
        }
      }
      throw new IllegalStateException("name not found");
    } catch (InvalidNameException e) {
      throw new RuntimeException(e);
    }
  }


  private KeyStore loadRootCA(InputStream inputStream, String password) {
    try {
      KeyStore keyStore = KeyStore.getInstance("JKS");
      char[] passwordAsCharArray = password == null ? null : password.toCharArray();
      keyStore.load(inputStream, passwordAsCharArray);
      return keyStore;
    } catch (NoSuchAlgorithmException
        | CertificateException
        | KeyStoreException
        | IOException e) {
      throw new RuntimeException("Unable to load JKS KeyStore", e);
    }
  }


  private KeyStore loadPkcsKeyStore(byte[] keyStoreData, String password) {
    try {
      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      char[] passwordAsCharArray = password == null ? null : password.toCharArray();
      keyStore.load(new ByteArrayInputStream(keyStoreData), passwordAsCharArray);
      return keyStore;
    } catch (NoSuchAlgorithmException
        | CertificateException
        | KeyStoreException
        | IOException e) {
      throw new RuntimeException("Unable to load JKS KeyStore", e);
    }
  }
}
