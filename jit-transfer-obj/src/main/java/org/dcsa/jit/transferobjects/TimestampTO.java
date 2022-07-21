package org.dcsa.jit.transferobjects;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.*;
import org.dcsa.skernel.infrastructure.validation.ValidVesselIMONumber;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

public record TimestampTO(
  @NotNull PartyTO publisher,
  @NotNull PublisherRole publisherRole,
  @Deprecated @ValidVesselIMONumber String vesselIMONumber, // Deprecated in JIT 1.2
  @NotNull @Size(max = 5) String UNLocationCode,
  @Deprecated @Size(max = 6) String facilitySMDGCode, // Deprecated in JIT 1.2
  FacilityTypeCodeOPR facilityTypeCode,
  @NotNull EventClassifierCode eventClassifierCode,
  @NotNull OperationsEventTypeCode operationsEventTypeCode,
  LocationTO eventLocation,
  LocationTO vesselPosition,
  @Deprecated ModeOfTransport modeOfTransport, // Deprecated in JIT 1.2
  PortCallServiceTypeCode portCallServiceTypeCode,
  PortCallPhaseTypeCode portCallPhaseTypeCode,
  @NotNull OffsetDateTime eventDateTime,
  @Deprecated @Size(max = 50) String exportVoyageNumber, // Deprecated in JIT 1.2
  @Deprecated @Size(max = 50) String importVoyageNumber, // Deprecated in JIT 1.2
  @Deprecated @Size(max = 50) String carrierVoyageNumber, // Deprecated in JIT 1.2 & hence removed NotNull constraint
  @NotNull @Size(max = 5) String carrierServiceCode,
  Integer transportCallSequenceNumber,
  String remark,
  String delayReasonCode,
  TimestampVesselTO vessel,
  @JsonAlias({"milesRemainingToDestination", "milesToDestinationPort"})
  Float milesToDestinationPort,
  @Size(max = 50) String portVisitReference
) {
  @Builder(toBuilder = true) // workaround for intellij issue
  public TimestampTO {}
}
