package org.dcsa.jit.transferobjects;

import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.CarrierCodeListProvider;
import org.dcsa.skernel.infrastructure.validation.ValidVesselIMONumber;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TransportCallVesselTO(
  @NotNull @ValidVesselIMONumber String vesselIMONumber,
  @Size(max = 35) String vesselName,
  @Size(max = 2) String vesselFlag,
  @Size(max = 10) String vesselCallSignNumber,
  @Size(max = 10) String vesselOperatorCarrierCode,
  CarrierCodeListProvider vesselOperatorCarrierCodeListProvider
){
  @Builder(toBuilder = true) // workaround for intellij issue
  public TransportCallVesselTO {}
}
