package org.dcsa.jit.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.FacilityCodeListProvider;

import javax.validation.constraints.Size;

public record LocationTO(
  @Size(max = 100) String locationName,
  @Size(max = 10) String latitude,
  @Size(max = 11) String longitude,
  @Size(max = 5) @JsonProperty("UNLocationCode") String unLocationCode,
  @Size(max = 6) String facilityCode,
  FacilityCodeListProvider facilityCodeListProvider,
  AddressTO addressTO,
  String id
  // Use Facility (and not FacilityTO) to avoid infinite recursion with FacilityTO and because we do not need a
  // full FacilityTO object.
 // private FacilityTO facility;
) {
  @Builder(toBuilder = true) // workaround for intellij issue
  public LocationTO { }


}
