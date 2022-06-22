package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.enums.FacilityTypeCodeTRN;
import org.dcsa.jit.persistence.entity.enums.PortCallStatusCode;
import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.entity.Location;

import javax.persistence.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "transport_call")
public class TransportCall {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "transport_call_reference", length = 100, nullable = false)
  private String transportCallReference;

  @Column(name = "transport_call_sequence_number")
  private Integer transportCallSequenceNumber;

  @Deprecated
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "facility_id")
  private Facility facility;

  @Enumerated(EnumType.STRING)
  @Column(name = "facility_type_code", length = 4, columnDefinition = "bpchar") // "bpchar" here is not a typing error
  private FacilityTypeCodeTRN facilityTypeCode;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "location_id")
  private Location location;

  @Column(name = "mode_of_transport_code", length = 3)
  private String modeOfTransportCode;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "vessel_id")
  private Vessel vessel;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "import_voyage_id")
  private Voyage importVoyage;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "export_voyage_id")
  private Voyage exportVoyage;

  @Enumerated(EnumType.STRING)
  @Column(name = "port_call_status_code", length = 4, columnDefinition = "bpchar") // "bpchar" here is not a typing error
  private PortCallStatusCode portCallStatusCode;
}
