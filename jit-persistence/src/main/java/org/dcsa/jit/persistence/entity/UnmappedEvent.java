package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "unmapped_event_queue")
public class UnmappedEvent implements Persistable {
  @Id
  @GeneratedValue
  @Column(name = "event_id", nullable = false)
  private UUID eventID;

  private transient boolean newRecord;

  @Column(name = "enqueued_at_date_time", nullable = false)
  private OffsetDateTime enqueuedAtDateTime;

  @Override
  public Object getId() {
    return null;
  }

  @Override
  public boolean isNew() {
    return newRecord;
  }
}
