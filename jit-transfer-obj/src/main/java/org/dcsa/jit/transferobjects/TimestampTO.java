package org.dcsa.jit.transferobjects;

import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.*;
import org.dcsa.skernel.infrastructure.validation.ValidVesselIMONumber;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

public record TimestampTO(
  @NotNull PartyTO publisher,
  @NotNull PublisherRole publisherRole,
  @ValidVesselIMONumber String vesselIMONumber,
  @NotNull @Size(max = 5) String UNLocationCode,
  @Size(max = 6) String facilitySMDGCode,
  FacilityTypeCodeOPR facilityTypeCode,
  @NotNull EventClassifierCode eventClassifierCode,
  @NotNull OperationsEventTypeCode operationsEventTypeCode,
  LocationTO eventLocation,
  LocationTO vesselPosition,
  ModeOfTransport modeOfTransport,
  PortCallServiceTypeCode portCallServiceTypeCode,
  PortCallPhaseTypeCode portCallPhaseTypeCode,
  @NotNull OffsetDateTime eventDateTime,
  @Size(max = 50) String exportVoyageNumber,
  @Size(max = 50) String importVoyageNumber,
  @Size(max = 50) String carrierVoyageNumber,
  @NotNull @Size(max = 5) String carrierServiceCode,
  Integer transportCallSequenceNumber,
  String remark,
  String delayReasonCode,
  VesselTO vessel,
  Integer milesRemainingToDestination
  ) {
  @Builder(toBuilder = true) // workaround for intellij issue
  public TimestampTO {}
}
