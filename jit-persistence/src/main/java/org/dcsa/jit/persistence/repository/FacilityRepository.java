package org.dcsa.jit.persistence.repository;

import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.entity.enums.FacilityCodeListProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, UUID> {
  Optional<Facility> findByUnLocationCodeAndSmdgCode(String unLocationCode, String smdgCode);

  Optional<Facility> findByUNLocationCodeAndFacilityCodeListProviderAndFacilityCode(
      String unLocationCode,
      FacilityCodeListProvider facilityCodeListProvider,
      String facilityCode);
}
