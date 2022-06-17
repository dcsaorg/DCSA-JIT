package org.dcsa.jit.transferobjects;

import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.*;

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
  FacilityTypeCode facilityTypeCode,
  LocationTO eventLocation,
  PortCallServiceTypeCode portCallServiceTypeCode,
  PortCallPhaseTypeCode portCallPhaseTypeCode,
  @Size(max = 3) String delayReasonCode,
  String remark,
  TransportCallTO transportCall,
  LocationTO vesselPosition
) {
    @Builder(toBuilder = true) // workaround for intellij issue
  public OperationsEventTO { }
}
