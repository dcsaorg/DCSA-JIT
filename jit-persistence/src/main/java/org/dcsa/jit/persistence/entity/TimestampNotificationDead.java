package org.dcsa.jit.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "timestamp_notification_dead")
public class TimestampNotificationDead {

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
}
