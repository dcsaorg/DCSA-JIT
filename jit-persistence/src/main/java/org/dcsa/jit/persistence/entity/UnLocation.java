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
  @Size(max = 5)
  @Column(name="un_location_code")
  private String unLocationCode;

  @Size(max = 100)
  @Column(name="un_location_name")
  private String unLocationName;

  @Size(max = 3)
  @Column(name="location_code")
  private String locationCode;

  @Size(max = 2)
  @Column(name="country_code")
  private String countryCode;

  public Long getId() {
    return id;
  }
}
