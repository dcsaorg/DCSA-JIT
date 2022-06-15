package org.dcsa.jit.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.FacilityCodeListProvider;
import org.dcsa.jit.transferobjects.enums.FacilityTypeCode;
import org.dcsa.jit.transferobjects.enums.ModeOfTransport;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

public record TransportCallTO(
  @NotNull @Size(max = 100) String transportCallReference,
  @Size(max = 5) String carrierServiceCode,
  @Size(max = 50) String exportVoyageNumber,
  @Size(max = 50) String importVoyageNumber,
  Integer transportCallSequenceNumber,
  @Size(max = 5) String UNLocationCode,
  @Size(max = 6) String facilityCode,
  FacilityCodeListProvider facilityCodeListProvider,
  FacilityTypeCode facilityTypeCode,
  @Size(max = 50) String otherFacility,
  @NotNull ModeOfTransport modeOfTransport,
  LocationTO location,
  VesselTO vessel
) {
  @Builder // workaround for intellij issue
  public TransportCallTO {}
}
