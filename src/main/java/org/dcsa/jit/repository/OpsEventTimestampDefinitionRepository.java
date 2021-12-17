package org.dcsa.jit.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.jit.model.OpsEventTimestampDefinition;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OpsEventTimestampDefinitionRepository extends ExtendedRepository<OpsEventTimestampDefinition, UUID> {

    @Modifying
    @Query("UPDATE ops_event_timestamp_definition SET payload_id = :payloadID WHERE event_id = :eventID")
    Mono<Void> linkPayload(UUID eventID, UUID payloadID);
}
