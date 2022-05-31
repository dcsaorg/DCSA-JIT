package org.dcsa.jit.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.dcsa.skernel.domain.persistence.entity.Address;
import org.dcsa.skernel.domain.persistence.entity.enums.FacilityCodeListProvider;

import javax.validation.constraints.Size;

public record LocationTO(
  @Size(max = 100) String locationName,
  @Size(max = 10) String latitude,
  @Size(max = 11) String longitude,
  @Size(max = 5) @JsonProperty("UNLocationCode") String unLocationCode,
  @Size(max = 6) String facilityCode,
  FacilityCodeListProvider facilityCodeListProvider,
  Address address
) {
  @Builder // workaround for intellij issue
  public LocationTO { }
}
