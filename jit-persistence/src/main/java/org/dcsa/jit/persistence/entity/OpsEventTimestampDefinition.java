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
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "event_id", nullable = false)
  @MapsId
  OperationsEvent operationsEvent;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "timestamp_definition", nullable = false)
  TimestampDefinition timestampDefinition;

  // Declare it so there are no surprises, but we do not need it.
  @Column(name = "payload_id")
  private UUID payloadID;
}
