package org.dcsa.jit.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
