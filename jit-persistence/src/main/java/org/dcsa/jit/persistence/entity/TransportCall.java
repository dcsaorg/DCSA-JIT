package org.dcsa.jit.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dcsa.jit.persistence.entity.enums.PortCallStatusCode;
import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.skernel.domain.persistence.entity.enums.FacilityTypeCode;

import javax.persistence.CascadeType;
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
  private String reference;

  @Column(name = "transport_call_sequence_number")
  private Integer sequenceNumber;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "facility_id")
  private Facility facility;

  @Enumerated(EnumType.STRING)
  @Column(name = "facility_type_code", length = 4)
  private FacilityTypeCode facilityTypeCode;

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
  @Column(name = "port_call_status_code", length = 4)
  private PortCallStatusCode portCallStatusCode;
}
