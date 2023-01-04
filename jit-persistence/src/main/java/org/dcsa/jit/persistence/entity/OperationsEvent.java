package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.enums.*;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.skernel.domain.persistence.entity.enums.DimensionUnit;
import org.hibernate.annotations.GenerationTime;
import org.springframework.data.annotation.CreatedDate;
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
@Table(name = "operations_event")
public class OperationsEvent implements Persistable<UUID> {
  @Id
  @Column(name = "event_id", nullable = false)
  private UUID eventID;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_classifier_code", length = 3, nullable = false)
  private EventClassifierCode eventClassifierCode;

  @CreatedDate
  @org.hibernate.annotations.Generated(value = GenerationTime.INSERT)
  @Column(name = "event_created_date_time", nullable = false)
  private OffsetDateTime eventCreatedDateTime;

  @Column(name = "event_date_time", nullable = false)
  private OffsetDateTime eventDateTime;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "publisher_id", nullable = false)
  private Party publisher;

  @Enumerated(EnumType.STRING)
  @Column(name = "publisher_role", nullable = false)
  private PublisherRole publisherRole;

  @Enumerated(EnumType.STRING)
  @Column(name = "operations_event_type_code", nullable = false)
  private OperationsEventTypeCode operationsEventTypeCode;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_location_id")
  private Location eventLocation;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transport_call_id", nullable = false)
  private TransportCall transportCall;

  @Enumerated(EnumType.STRING)
  @Column(name = "port_call_service_type_code")
  private PortCallServiceTypeCode portCallServiceTypeCode;

  @Column(name = "delay_reason_code", length = 3)
  private String delayReasonCode;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vessel_position_id")
  private Location vesselPosition;

  @Column(name = "remark", length = 100)
  private String remark;

  @Enumerated(EnumType.STRING)
  @Column(name = "port_call_phase_type_code")
  @Setter
  private PortCallPhaseTypeCode portCallPhaseTypeCode;

  @Column(name = "facility_type_code")
  @Enumerated(EnumType.STRING)
  private FacilityTypeCodeOPR facilityTypeCode;

  @Column(name = "vessel_draft")
  private Float vesselDraft;

  @Enumerated(EnumType.STRING)
  @Column(name = "vessel_draft_unit")
  @Setter
  private DimensionUnit vesselDraftUnit;

  @Column(name = "miles_to_destination_port")
  private Float milesToDestinationPort;

  @Transient Boolean newRecord;
  @Override
  public boolean isNew() {
    return getId() != null || this.newRecord;
  }

  @Override
  public UUID getId() {
    return this.eventID;
  }
}
