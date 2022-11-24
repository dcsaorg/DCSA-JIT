package org.dcsa.jit.persistence.repository;

import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.repository.FacilityRepository;

import java.util.List;

public interface JITFacilityRepository extends FacilityRepository {
  List<Facility> findAllByFacilitySMDGCode(String smdgCode);
}
