package org.dcsa.jit.persistence.repository;

import org.dcsa.skernel.domain.persistence.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, UUID> {
  Carrier findBySmdgCode(String smdgCode);

  Carrier findByNmftaCode(String nmftaCode);
}
