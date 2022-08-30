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
@Table(name = "outbox_message")
@NamedQuery(name = "poll-outbox-messages", query = "SELECT om FROM OutboxMessage om")
public class OutboxMessage {
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
}
