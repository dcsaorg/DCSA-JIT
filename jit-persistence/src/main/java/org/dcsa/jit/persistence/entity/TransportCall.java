package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.enums.FacilityTypeCodeTRN;
import org.dcsa.jit.persistence.entity.enums.PortCallStatusCode;
import org.dcsa.skernel.domain.persistence.entity.Location;

import jakarta.persistence.*;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "transport_call")
@Inheritance(strategy = InheritanceType.JOINED) // for UI-Support
public class TransportCall {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "transport_call_reference", length = 100, nullable = false)
  private String transportCallReference;

  @Column(name = "transport_call_sequence_number")
  private Integer transportCallSequenceNumber;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Enumerated(EnumType.STRING)
  @Column(name = "facility_type_code", length = 4, columnDefinition = "bpchar") // "bpchar" here is not a typing error
  private FacilityTypeCodeTRN facilityTypeCode;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "location_id")
  private Location location;

  @Column(name = "mode_of_transport_code", length = 3)
  private String modeOfTransportCode;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "vessel_id")
  private Vessel vessel;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "import_voyage_id")
  private Voyage importVoyage;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "export_voyage_id")
  private Voyage exportVoyage;

  @Enumerated(EnumType.STRING)
  @Column(name = "port_call_status_type_code", length = 4, columnDefinition = "bpchar") // "bpchar" here is not a typing error
  private PortCallStatusCode portCallStatusCode;

  @Column(name="port_visit_reference", length=50)
  private String portVisitReference;

  // Added only for supporting TimestampInfoService/TimestampInfoController's "portVisitID" query param.
  @OneToOne
  @JoinTable(
    name = "transport_call_jit_port_visit",
    joinColumns = @JoinColumn(name = "transport_call_id")
    // Inverse is not possible
    // inverseJoinColumns = @JoinColumn(name = "port_visit_id")
  )
  private TransportCall portVisit;
}
