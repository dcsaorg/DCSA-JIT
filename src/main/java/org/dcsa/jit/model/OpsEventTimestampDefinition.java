package org.dcsa.jit.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("ops_event_timestamp_definition")
public class OpsEventTimestampDefinition {

    @Id
    @Column("event_id")
    private UUID eventID;

    @Column("timestamp_definition")
    private String timestampDefinition;
}
