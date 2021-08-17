package org.dcsa.ovs.model.transferobjects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.base.AbstractEventSubscription;
import org.dcsa.core.validator.EnumSubset;

@Data
@EqualsAndHashCode(callSuper = true)
public class OVSEventSubscriptionTO extends AbstractEventSubscription {

  @EnumSubset(anyOf = {"TRANSPORT", "OPERATIONS"})
  private String eventType;

  private String transportEventTypeCode;

  private String transportCallID;

  private String vesselIMONumber;

  private String carrierVoyageNumber;

  private String carrierServiceCode;

  private String operationsEventTypeCode;
}
