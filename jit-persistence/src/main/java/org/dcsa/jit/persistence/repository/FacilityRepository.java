package org.dcsa.jit.persistence.repository;

import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, UUID> {
  Optional<Facility> findByUNLocationCodeAndFacilitySMDGCode(String unLocationCode, String smdgCode);
  Optional<Facility> findByUNLocationCodeAndFacilityBICCode(String unLocationCode, String bicCode);

  List<Facility> findAllByFacilitySMDGCode(String smdgCode);
}
