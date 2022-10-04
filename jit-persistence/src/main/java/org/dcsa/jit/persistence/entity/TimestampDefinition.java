package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.enums.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@NamedEntityGraph(
  name = "timestampDefinition.allAttributes",
  includeAllAttributes = true
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "timestamp_definition")
public class TimestampDefinition {
  @Id
  @Column(name = "timestamp_id", nullable = false)
  private String id;

  @Column(name = "timestamp_type_name", nullable = false, unique = true)
  private String timestampTypeName;

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
  private FacilityTypeCodeOPR facilityTypeCode;

  @Column(name = "port_call_part", length = 100)
  private String portCallPart;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_location_requirement", nullable = false)
  private LocationRequirement eventLocationRequirement;

  @Column(name = "is_terminal_needed", nullable = false)
  private Boolean isTerminalNeeded;

  @Enumerated(EnumType.STRING)
  @Column(name = "vessel_position_requirement", nullable = false)
  private LocationRequirement vesselPositionRequirement;

  @OneToOne
  @JoinColumn(name = "negotiation_cycle")
  private NegotiationCycle negotiationCycle;

  @Column(name = "is_miles_to_destination_relevant", nullable = false)
  private Boolean isMilesToDestinationRelevant;

  @Column(name = "provided_in_standard", nullable = false)
  private String providedInStandard;

  @Column(name = "accept_timestamp_definition", nullable = false)
  private String acceptTimestampDefinition;

  @Column(name = "reject_timestamp_definition", nullable = false)
  private String rejectTimestampDefinition;

  @Column(name = "implicit_variant_of", nullable = false)
  private String implicitVariantOf;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToMany
  @JoinTable
    (
      name="timestamp_definition_publisher_pattern",
      joinColumns={ @JoinColumn(name="timestamp_id", referencedColumnName="timestamp_id") },
      inverseJoinColumns={ @JoinColumn(name="pattern_id", referencedColumnName="pattern_id", unique=true) }
    )
  private Set<PublisherPattern> publisherPattern = new LinkedHashSet<>();

}
