package org.dcsa.jit.transferobjects;

import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.DimensionUnit;
import org.dcsa.jit.transferobjects.enums.VesselType;
import org.dcsa.skernel.infrastructure.validation.ValidVesselIMONumber;

public record TimestampVesselTO (
  @ValidVesselIMONumber String vesselIMONumber,
  String name,
  Float lengthOverall,
  Float width,
  String callSign,
  VesselType type,
  Float draft,
  DimensionUnit dimensionUnit
){
  @Builder(toBuilder = true) // workaround for intellij issue
  public TimestampVesselTO {}
}
