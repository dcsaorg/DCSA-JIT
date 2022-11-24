package org.dcsa.jit.transferobjects;

import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.*;
import org.dcsa.skernel.infrastructure.transferobject.LocationTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OperationsEventTO(
  UUID eventID,
  @NotNull OffsetDateTime eventCreatedDateTime,
  @NotNull String eventType,
  @NotNull EventClassifierCode eventClassifierCode,
  @NotNull OffsetDateTime eventDateTime,
  @NotNull OperationsEventTypeCode operationsEventTypeCode,
  @NotNull PartyTO publisher,
  @NotNull PublisherRole publisherRole,
  FacilityTypeCodeOPR facilityTypeCode,
  LocationTO eventLocation,
  PortCallServiceTypeCode portCallServiceTypeCode,
  PortCallPhaseTypeCode portCallPhaseTypeCode,
  @Size(max = 3) String delayReasonCode,
  String remark,
  TransportCallTO transportCall,
  LocationTO vesselPosition,
  Float vesselDraft,
  DimensionUnit vesselDraftUnit,
  @Deprecated Float milesRemainingToDestination, // Deprecated in JIT 1.2
  Float milesToDestinationPort // same as milesRemainingToDestination however we return both as we cannot distinguish between the versions
) {
    @Builder(toBuilder = true) // workaround for intellij issue
  public OperationsEventTO { }
}
