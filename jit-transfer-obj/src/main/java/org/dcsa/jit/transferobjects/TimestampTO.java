package org.dcsa.jit.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.dcsa.jit.enums.ModeOfTransport;
import org.dcsa.jit.enums.PortCallPhaseTypeCode;
import org.dcsa.jit.enums.PortCallServiceTypeCode;
import org.dcsa.jit.enums.PublisherRole;
import org.dcsa.skernel.domain.persistence.entity.enums.FacilityTypeCode;
import org.dcsa.skernel.infrastructure.validation.ValidVesselIMONumber;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

public record TimestampTO(
  @NotNull PartyTO publisher,
  @NotNull PublisherRole publisherRole,
  @NotNull @ValidVesselIMONumber String vesselIMONumber,
  @NotNull @Size(max = 5) @JsonProperty("UNLocationCode") String unLocationCode,
  FacilityTypeCode facilityTypeCode,
  String eventClassifierCode,
  String operationsEventTypeCode,
  LocationTO eventLocation,
  LocationTO vesselPosition,
  ModeOfTransport modeOfTransport,
  PortCallServiceTypeCode portCallServiceTypeCode,
  PortCallPhaseTypeCode portCallPhaseTypeCode,
  @NotNull OffsetDateTime eventDateTime,
  @Size(max = 50) String exportVoyageNumber,
  @Size(max = 50) String importVoyageNumber,
  @NotNull @Size(max = 5) String carrierServiceCode,
  Integer transportCallSequenceNumber,
  String remark,
  String delayReasonCode
) {
  @Builder // workaround for intellij issue
  public TimestampTO {}
}
