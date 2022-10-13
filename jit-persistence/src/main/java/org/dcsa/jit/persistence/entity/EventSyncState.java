package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.enums.DeliveryStatus;

import javax.persistence.*;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "event_sync_state")
public class EventSyncState {

  @Id
  @Column(name = "event_id")
  private UUID eventID;

  @Enumerated(EnumType.STRING)
  @Column(name ="delivery_status")
  private DeliveryStatus deliveryStatus;
}
