package org.dcsa.jit.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;
import org.dcsa.jit.persistence.entity.enums.OperationsEventTypeCode;
import org.dcsa.jit.persistence.entity.enums.PortCallPhaseTypeCode;
import org.dcsa.jit.persistence.entity.enums.PortCallServiceTypeCode;
import org.dcsa.jit.persistence.entity.enums.PublisherRole;
import org.dcsa.skernel.domain.persistence.entity.enums.FacilityTypeCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "timestamp_definition")
public class TimestampDefinition {
  @Id
  @Column(name = "id", nullable = false)
  private String id;

  @Column(name = "timestamp_type_name", nullable = false, unique = true)
  private String timestampTypeName;

  @Enumerated(EnumType.STRING)
  @Column(name = "publisher_role", length = 3, nullable = false)
  private PublisherRole publisherRole;

  @Enumerated(EnumType.STRING)
  @Column(name = "primary_receiver", length = 3, nullable = false)
  private PublisherRole primaryReceiver;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_classifier_code", length = 3, nullable = false)
  private EventClassifierCode eventClassifierCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "operations_event_type_code", length = 4, nullable = false)
  private OperationsEventTypeCode operationsEventTypeCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "port_call_phase_type_code", length = 4)
  private PortCallPhaseTypeCode portCallPhaseTypeCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "port_call_service_type_code", length = 4)
  private PortCallServiceTypeCode portCallServiceTypeCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "facility_type_code", length = 4)
  private FacilityTypeCode facilityTypeCode;

  @Column(name = "is_berth_location_needed", nullable = false)
  private Boolean isBerthLocationNeeded;

  @Column(name = "is_pbp_location_needed", nullable = false)
  private Boolean isPBPLocationNeeded;

  @Column(name = "is_terminal_needed", nullable = false)
  private Boolean isTerminalNeeded;

  @Column(name = "is_vessel_position_needed", nullable = false)
  private Boolean isVesselPositionNeeded;

  @Column(name = "negotiation_cycle", nullable = false)
  private String negotiationCycle;

  @Column(name = "provided_in_standard", nullable = false)
  private String providedInStandard;

  @Column(name = "accept_timestamp_definition", nullable = false)
  private String acceptTimestampDefinition;

  @Column(name = "reject_timestamp_definition", nullable = false)
  private String rejectTimestampDefinition;
}
