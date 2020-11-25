package com.example.mtls.client;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

@RestController
public class RootController {

    private final CertService certService;
    private final CertRepository certRepository;

    public RootController(CertService certService, CertRepository certRepository) throws Exception {
        this.certService = certService;
        this.certRepository = certRepository;
    }

    @GetMapping("/certs")
    public List<CertEntity> certs(){
        List<CertEntity> certs = new ArrayList<>();
        this.certRepository.findAll().forEach(certs::add);
        return certs;
    }

    @GetMapping("/")
    public String homePage()
    {
        return this.certService.getStoreRestTemplate("store-100","password")
            .map(restTemplate -> restTemplate.getForObject("https://localhost:8443/", String.class ))
            .orElse("No rest template found for an mTLS call");
    }

    @GetMapping("/100")
    public String store100Call()
    {
        return this.certService.getStoreRestTemplate("store-100","password")
            .map(restTemplate -> restTemplate.getForObject("https://localhost:8443/", String.class ))
            .orElse("No rest template found for an mTLS call");
    }

    @GetMapping("/200")
    public String store200Call()
    {
        return this.certService.getStoreRestTemplate("store-200","password")
            .map(restTemplate -> restTemplate.getForObject("https://localhost:8443/", String.class ))
            .orElse("No rest template found for an mTLS call");
    }
}
