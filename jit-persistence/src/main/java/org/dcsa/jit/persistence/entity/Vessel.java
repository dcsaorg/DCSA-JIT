package org.dcsa.jit.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "vessel")
public class Vessel {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "vessel_imo_number", length = 7, unique = true)
  private String imoNumber;

  @Column(name = "vessel_name", length = 35)
  private String name;

  @Column(name = "vessel_flag", length = 2)
  private String flag;

  @Column(name = "vessel_call_sign", length = 18)
  private String callSign;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vessel_operator_carrier_id")
  private Carrier operatorCarrier;

  @Column(name = "is_dummy")
  private Boolean isDummy;

  @Column(name = "length")
  private Float length;

  @Column(name = "width")
  private Float width;

  @Column(name = "dimension_unit", length = 3)
  private String dimensionUnit;
}
