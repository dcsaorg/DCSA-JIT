package org.dcsa.jit.persistence.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Only need the id to check if the entry exists
 */
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "pending_event_queue")
@Getter
public class PendingEvent {
  @Id
  @Column(name = "delivery_id", nullable = false)
  private UUID deliveryID;

  @Column(name = "event_id", nullable = false)
  private UUID eventID;

  @Column(name = "subscription_id", nullable = false)
  private UUID subscriptionID;

  @Column(name = "payload", nullable = false)
  private String payload;

  @Column(name = "enqueued_at_date_time", nullable = false)
  private ZonedDateTime enqueuedAtDateTime;

  @Column(name = "last_attempt_date_time")
  private ZonedDateTime lastAttemptDateTime;

  @Column(name = "last_error_message")
  private String lastErrorMessage;

  @Column(name = "retry_count", nullable = false)
  private Integer retryCount;
}
