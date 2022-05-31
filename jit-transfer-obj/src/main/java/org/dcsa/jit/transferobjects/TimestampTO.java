package org.dcsa.jit.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.dcsa.jit.persistence.entity.Party;
import org.dcsa.skernel.domain.persistence.entity.enums.FacilityTypeCode;
import org.dcsa.skernel.infrastructure.validation.ValidVesselIMONumber;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

public record TimestampTO(
  @NotNull Party publisher,
  @NotNull String publisherRole,
  @NotNull @ValidVesselIMONumber String vesselIMONumber,
  @NotNull @Size(max = 5) @JsonProperty("UNLocationCode") String unLocationCode,
  FacilityTypeCode facilityTypeCode,
  String eventClassifierCode,
  String operationsEventTypeCode,
  LocationTO eventLocation,
  @NotNull OffsetDateTime eventDateTime,
  String remark,
  String delayReasonCode
) {
  @Builder // workaround for intellij issue
  public TimestampTO {}
}
