package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.OpsEventTimestampDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OpsEventTimestampDefinitionRepository
    extends JpaRepository<OpsEventTimestampDefinition, UUID> {}
