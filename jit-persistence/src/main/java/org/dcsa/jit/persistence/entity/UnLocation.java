package org.dcsa.jit.persistence.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name="un_location")
public class UnLocation {

  @Id
  @Column(name="un_location_code", length = 5, columnDefinition = "bpchar") // "bpchar" here is not a typing error
  private String unLocationCode;

  @Column(name="un_location_name", length = 100)
  private String unLocationName;

  @Column(name="location_code", columnDefinition = "bpchar")
  private String locationCode;

  @Column(name="country_code", columnDefinition = "bpchar") // "bpchar" here is not a typing error
  private String countryCode;

}
