package org.dcsa.jit.model.transferobjects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.base.AbstractEventSubscription;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.model.enums.OperationsEventTypeCode;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.dcsa.core.validator.EnumSubset;
import org.dcsa.core.validator.ValidVesselIMONumber;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class JITEventSubscriptionTO extends AbstractEventSubscription {

  @EnumSubset(anyOf = {"TRANSPORT", "OPERATIONS"})
  private List<EventType> eventType;

  private List<TransportEventTypeCode> transportEventTypeCode;

  private String transportCallID;

  @ValidVesselIMONumber(allowNull = true)
  private String vesselIMONumber;

  private String carrierVoyageNumber;

  private String carrierServiceCode;

  private List<OperationsEventTypeCode> operationsEventTypeCode;
}
