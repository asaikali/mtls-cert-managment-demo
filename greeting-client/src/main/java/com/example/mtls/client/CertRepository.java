package com.example.mtls.client;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

interface CertRepository extends CrudRepository<CertEntity,Integer> {

  @Query("SELECT c from CertEntity c WHERE c.store=:storeId")
  List<CertEntity> findStoreCerts(String storeId);
}
