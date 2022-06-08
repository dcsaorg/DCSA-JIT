package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.UnmappedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UnmappedEventRepository extends JpaRepository<UnmappedEvent, UUID> {}
