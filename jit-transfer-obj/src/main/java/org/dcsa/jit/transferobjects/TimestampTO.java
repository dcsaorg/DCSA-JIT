package org.dcsa.jit.transferobjects;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.*;
import org.dcsa.skernel.infrastructure.transferobject.LocationTO;
import org.dcsa.skernel.infrastructure.validation.ValidVesselIMONumber;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.beans.Transient;
import java.time.OffsetDateTime;

public record TimestampTO(
  @Valid @NotNull PartyTO publisher,
  @NotNull PublisherRole publisherRole,
  @Deprecated @ValidVesselIMONumber String vesselIMONumber, // Deprecated in JIT 1.2
  @NotNull @Size(max = 5) String UNLocationCode,
  @Deprecated @Size(max = 6) String facilitySMDGCode, // Deprecated in JIT 1.2
  FacilityTypeCodeOPR facilityTypeCode,
  @NotNull EventClassifierCode eventClassifierCode,
  @NotNull OperationsEventTypeCode operationsEventTypeCode,
  @Valid LocationTO eventLocation,
  @Valid LocationTO.GeoLocationTO vesselPosition,
  @Deprecated ModeOfTransport modeOfTransport, // Deprecated in JIT 1.2
  PortCallServiceTypeCode portCallServiceTypeCode,
  PortCallPhaseTypeCode portCallPhaseTypeCode,
  @NotNull OffsetDateTime eventDateTime,
  @Deprecated @Size(max = 50) String exportVoyageNumber, // Deprecated in JIT 1.2
  @Deprecated @Size(max = 50) String importVoyageNumber, // Deprecated in JIT 1.2
  @Deprecated @NotNull @Size(max = 50) String carrierVoyageNumber, // Deprecated in JIT 1.2
  @Size(max = 50) String carrierExportVoyageNumber,
  @Size(max = 50) String carrierImportVoyageNumber,
  @NotNull @Size(max = 5) String carrierServiceCode,
  Integer transportCallSequenceNumber,
  String remark,
  @Size(max = 3) String delayReasonCode,
  @Valid TimestampVesselTO vessel,
  @JsonAlias({"milesRemainingToDestination", "milesToDestinationPort"})
  Float milesToDestinationPort,
  @Size(max = 50) String portVisitReference
) {
  @Builder(toBuilder = true) // workaround for intellij issue
  public TimestampTO {}

  /**
   * Canonical way to get vesselIMONumber that will work on both jit 1.2 and pre jit 1.2 and won't
   * break if vesselIMONumber is made optional (ie. if we get rid of forward compatibility requirement).
   */
  @Transient
  public String canonicalVesselIMONumber() {
    if (vessel != null && vessel.vesselIMONumber() != null) {
      return vessel.vesselIMONumber(); // jit 1.2
    } else {
      return vesselIMONumber; // pre jit 1.2
    }
  }
}
