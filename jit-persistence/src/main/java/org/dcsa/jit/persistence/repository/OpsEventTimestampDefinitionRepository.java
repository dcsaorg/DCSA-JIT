package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.OpsEventTimestampDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OpsEventTimestampDefinitionRepository
    extends JpaRepository<OpsEventTimestampDefinition, UUID>,
            JpaSpecificationExecutor<OpsEventTimestampDefinition> {

}
