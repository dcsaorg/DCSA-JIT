package org.dcsa.jit.notifications;

import lombok.experimental.UtilityClass;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;

import java.util.UUID;

/**
 * Not with other datafactories since it does not generate valid data.
 */
@UtilityClass
public class MailNotificationsOperationsEventDataFactory {
  public static OperationsEvent operationsEvent() {
    return OperationsEvent.builder()
      .transportCall(TransportCall.builder()
        .id(UUID.fromString("414f91c2-650c-4f73-82cb-bd1171296140"))
        .vessel(Vessel.builder()
          .vesselIMONumber("1234567")
          .name("my-vessel-name")
          .build())
        .portVisit(TransportCall.builder()
          .id(UUID.fromString("5db49152-a0d3-41c6-af1d-219c0eb7abcd"))
          .build())
        .build())
      .eventClassifierCode(EventClassifierCode.ACT)
      .build();
  }
}
