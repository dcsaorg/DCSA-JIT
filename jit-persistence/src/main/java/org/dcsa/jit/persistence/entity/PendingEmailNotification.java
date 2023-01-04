package org.dcsa.jit.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "pending_email_notification")
@NamedQuery(
  name = "PendingEmailNotification.nextPendingEmailNotifications",
  query = "FROM PendingEmailNotification"
)
public class PendingEmailNotification {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "event_id", nullable = false)
  private UUID eventID;

  @Column(name = "template_name", nullable = false)
  private String templateName;

  @Column(name = "enqueued_at_date_time", nullable = false)
  private OffsetDateTime enqueuedAt;
}
