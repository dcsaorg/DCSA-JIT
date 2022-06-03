package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.Vessel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VesselRepository extends JpaRepository<Vessel, UUID> {
  // TODO when valid_until is implemented use custom query to only look up validUntil == null
  Optional<Vessel> findByImoNumber(String imoNumber);
}
