package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.enums.OperationsEventTypeCode;
import org.dcsa.jit.persistence.entity.enums.PortCallPhaseTypeCode;
import org.dcsa.jit.persistence.entity.enums.PortCallServiceTypeCode;
import org.dcsa.jit.persistence.entity.enums.PublisherRole;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.skernel.domain.persistence.entity.enums.FacilityTypeCode;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "operations_event")
public class OperationsEvent {
  @Id
  @GeneratedValue
  @Column(name = "event_id", nullable = false)
  private UUID id;

  @Column(name = "event_classifier_code", length = 3, nullable = false)
  private String classifierCode;

  @Column(name = "event_created_date_time", nullable = false)
  private OffsetDateTime createdDateTime;

  @Column(name = "event_date_time", nullable = false)
  private OffsetDateTime dateTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "publisher", nullable = false)
  private Party publisher;

  @Enumerated(EnumType.STRING)
  @Column(name = "publisher_role", nullable = false)
  private PublisherRole publisherRole;

  @Enumerated(EnumType.STRING)
  @Column(name = "operations_event_type_code", nullable = false)
  private OperationsEventTypeCode operationsEventTypeCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_location")
  private Location location;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transport_call_id", nullable = false)
  private TransportCall transportCall;

  @Enumerated(EnumType.STRING)
  @Column(name = "port_call_service_type_code")
  private PortCallServiceTypeCode portCallServiceTypeCode;

  @Column(name = "delay_reason_code", length = 4)
  private String delayReasonCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vessel_position")
  private Location vesselPosition;

  @Column(name = "remark", length = 100)
  private String remark;

  @Enumerated(EnumType.STRING)
  @Column(name = "port_call_phase_type_code")
  private PortCallPhaseTypeCode portCallPhaseTypeCode;

  @Column(name = "facility_type_code")
  @Enumerated(EnumType.STRING)
  private FacilityTypeCode facilityTypeCode;
}
