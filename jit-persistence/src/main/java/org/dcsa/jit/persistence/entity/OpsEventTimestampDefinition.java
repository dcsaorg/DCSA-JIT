package org.dcsa.jit.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "ops_event_timestamp_definition")
public class OpsEventTimestampDefinition {
  @Id
  @Column(name = "event_id", nullable = false)
  private UUID eventID;

  @Column(name = "timestamp_definition", nullable = false)
  private String timestampDefinitionID;
}
