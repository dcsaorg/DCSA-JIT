package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "unmapped_event_queue")
public class UnmappedEvent {
  @Id
  @GeneratedValue
  @Column(name = "event_id", nullable = false)
  private UUID eventID;

  private boolean newRecord;

  @Column(name = "enqueued_date_time", nullable = false)
  private OffsetDateTime enqueuedAtDateTime;
}
