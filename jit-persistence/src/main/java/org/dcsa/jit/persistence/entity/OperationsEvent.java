package org.dcsa.jit.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dcsa.jit.persistence.entity.enums.*;
import org.dcsa.skernel.domain.persistence.entity.Location;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
  private PartyFunction publisherRole;

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

  @Column(name="facility_type_code")
  @Enumerated(EnumType.STRING)
  private FacilityTypeCode facilityTypeCode;
}
