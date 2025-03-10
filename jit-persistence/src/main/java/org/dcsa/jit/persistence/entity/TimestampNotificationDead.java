package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "timestamp_notification_dead")
public class TimestampNotificationDead implements Persistable<UUID> {

  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "message_routing_rule_id", nullable = false)
  private MessageRoutingRule messageRoutingRule;

  @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
  private String
      payload; // String as payload since persistence module should not import transfer-obj

  @Column(name = "latest_delivery_attempted_datetime")
  private OffsetDateTime latestDeliveryAttemptedDatetime;

  @Transient
  private boolean isNew;

  public boolean isNew() {
    return id == null || isNew;
  }

  public static TimestampNotificationDead from(OutboxMessage outboxMessage) {
    return TimestampNotificationDead.builder()
      // Preserve the ID to make tracking easier across fail-retry cycles.
      .id(outboxMessage.getId())
      .messageRoutingRule(outboxMessage.getMessageRoutingRule())
      .payload(outboxMessage.getPayload())
      .latestDeliveryAttemptedDatetime(OffsetDateTime.now())
      .isNew(true)
      .build();
  }
}
