package org.dcsa.jit.notifications;

import lombok.experimental.UtilityClass;
import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;
import org.dcsa.jit.persistence.entity.enums.FacilityTypeCodeOPR;
import org.dcsa.jit.persistence.entity.enums.LocationRequirement;
import org.dcsa.jit.persistence.entity.enums.OperationsEventTypeCode;

@UtilityClass
public class TimestampDefinitionDataFactory {

  public static TimestampDefinition timestampDefinition() {
    return TimestampDefinition.builder()
        .id("84d744d0-e867-4c88-a869-9b88fbe3239d")
        .timestampTypeName("ETA-Berth")
        .eventClassifierCode(EventClassifierCode.ACT)
        .facilityTypeCode(FacilityTypeCodeOPR.BRTH)
        .isTerminalNeeded(false)
        .acceptTimestampDefinition(String.valueOf(true))
        .operationsEventTypeCode(OperationsEventTypeCode.ARRI)
        .eventLocationRequirement(LocationRequirement.OPTIONAL)
        .vesselPositionRequirement(LocationRequirement.OPTIONAL)
        .build();
  }
}
