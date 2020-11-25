package com.example.mtls.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class Loader implements CommandLineRunner {

  private final  CertService certService;

  Loader(CertService certService) {
    this.certService = certService;
  }

  @Override
  public void run(String... args) throws Exception {
    Files.newDirectoryStream(Paths.get("greeting-client/certs")).forEach( path -> {
      try {
        byte[] bytes = Files.readAllBytes(path);
        this.certService.addCert(bytes,"password");
      } catch (IOException e) {
       throw new RuntimeException(e);
      }
    });
  }
}
