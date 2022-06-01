package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.enums.*;
import org.dcsa.jit.persistence.entity.enums.PartyFunction;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name="timestamp_definition")
public class TimestampDefinition {

    @Id
    @Column(name="id", nullable = false)
    private String id;

    @Column(name="timestamp_type_name")
    private String timestampTypeName;

    @Column(name="publisher_role")
    @Enumerated(EnumType.STRING)
    private PartyFunction publisherRole;

    @Column(name="primary_receiver")
    @Enumerated(EnumType.STRING)
    private PartyFunction primaryReceiver;

    @Column(name="event_classifier_code")
    private EventClassifierCode eventClassifierCode;

    @Column(name="operations_event_type_code")
    private OperationsEventTypeCode operationsEventTypeCode;

    @Column(name="port_call_phase_type_code")
    @Enumerated(EnumType.STRING)
    private PortCallPhaseTypeCode portCallPhaseTypeCode;

    @Column(name="port_call_service_type_code")
    @Enumerated(EnumType.STRING)
    private PortCallServiceTypeCode portCallServiceTypeCode;

    @Column(name="facility_type_code")
    @Enumerated(EnumType.STRING)
    private FacilityTypeCode facilityTypeCode;

    @Column(name="is_berth_location_needed")
    private Boolean isBerthLocationNeeded;

    @Column(name="is_pbp_location_needed")
    private Boolean isPBPLocationNeeded;

    @Column(name="is_terminal_needed")
    private Boolean isTerminalNeeded;

    @Column(name="is_vessel_position_needed")
    private Boolean isVesselPositionNeeded;

    @Column(name="negotiation_cycle")
    private String negotiationCycle;

    @Column(name="provided_in_standard")
    private String providedInStandard;

    @Column(name="accept_timestamp_definition")
    private String acceptTimestampDefinition;

    @Column(name="reject_timestamp_definition")
    private String rejectTimestampDefinition;

}
