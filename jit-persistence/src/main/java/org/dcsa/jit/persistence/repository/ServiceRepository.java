package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {
  List<Service> findByCarrierServiceCode(String carrierServiceCode);
}
