package org.dcsa.jit.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.base.AbstractEventSubscription;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.model.enums.OperationsEventTypeCode;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.dcsa.core.validator.EnumSubset;
import org.dcsa.skernel.validator.ValidVesselIMONumber;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class JITEventSubscriptionTO extends AbstractEventSubscription {

  @EnumSubset(anyOf = {"OPERATIONS"})
  private List<EventType> eventType;

  private List<TransportEventTypeCode> transportEventTypeCode;

  // It is a reference, but in the API it was released ID, so we are stuck with this
  // mismatch until we can clean it up in a breaking change.
  @JsonProperty("transportCallID")
  private String transportCallReference;

  @ValidVesselIMONumber(allowNull = true)
  private String vesselIMONumber;

  private String carrierVoyageNumber;

  private String carrierServiceCode;

  private List<OperationsEventTypeCode> operationsEventTypeCode;
}
