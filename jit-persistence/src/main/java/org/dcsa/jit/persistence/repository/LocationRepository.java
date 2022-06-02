package org.dcsa.jit.persistence.repository;

import org.dcsa.skernel.domain.persistence.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, String> {}
