package org.dcsa.jit.persistence.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "outbox_message")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class OutboxMessage {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private UUID id;

  @EqualsAndHashCode.Exclude
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "message_routing_rule_id", nullable = false)
  private MessageRoutingRule messageRoutingRule;

  @Type(type = "jsonb")
  @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
  private String payload; // String as payload since persistence module should not import transfer-obj

  @Column(name = "latest_delivery_attempted_datetime")
  private OffsetDateTime latestDeliveryAttemptedDatetime;
}
