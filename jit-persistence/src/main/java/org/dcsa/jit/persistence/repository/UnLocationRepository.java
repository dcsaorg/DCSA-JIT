package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.UnLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnLocationRepository extends JpaRepository<UnLocation, String> {}
