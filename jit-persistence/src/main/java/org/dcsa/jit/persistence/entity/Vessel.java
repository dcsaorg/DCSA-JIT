package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.enums.VesselType;
import org.dcsa.skernel.domain.persistence.entity.Carrier;

import javax.persistence.*;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "vessel")
public class Vessel {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "vessel_imo_number", length = 7, unique = true)
  private String vesselIMONumber;

  @Column(name = "vessel_name", length = 35)
  private String vesselName;

  @Column(
      name = "vessel_flag",
      length = 2,
      columnDefinition = "bpchar") // "bpchar" here is not a typing error)
  private String vesselFlag;

  @Column(name = "vessel_call_sign", length = 18)
  private String vesselCallSignNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vessel_operator_carrier_id")
  private Carrier vesselOperatorCarrier;

  @Column(name = "is_dummy")
  private Boolean isDummy;

  @Column(name = "length", columnDefinition = "numeric")
  private Float length;

  @Column(name = "width", columnDefinition = "numeric")
  private Float width;

  @Column(name = "dimension_unit", length = 3)
  private String dimensionUnit;
}
