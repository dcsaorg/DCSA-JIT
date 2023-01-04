package org.dcsa.jit.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "timestamp_definition", nullable = false)
  private TimestampDefinition timestampDefinition;
}
