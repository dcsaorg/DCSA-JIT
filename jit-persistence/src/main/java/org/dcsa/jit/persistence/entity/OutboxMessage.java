package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.*;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "outbox_message")
@NamedQuery(name = "poll-outbox-messages", query = "SELECT om FROM OutboxMessage om")
public class OutboxMessage implements Persistable<UUID> {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private UUID id;

  @EqualsAndHashCode.Exclude
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "message_routing_rule_id", nullable = false)
  private MessageRoutingRule messageRoutingRule;

  @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
  private String
      payload; // String as payload since persistence module should not import transfer-obj

  @Transient
  private boolean isNew;

  public boolean isNew() {
    return id == null || isNew;
  }

  public static OutboxMessage retry(TimestampNotificationDead dead) {
    return OutboxMessage.builder()
      .id(dead.getId())
      .messageRoutingRule(dead.getMessageRoutingRule())
      .payload(dead.getPayload())
      .isNew(true)
      .build();
  }

}
