package org.dcsa.jit.persistence.repository;

import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FacilityRepository extends JpaRepository<Facility, UUID> {}
