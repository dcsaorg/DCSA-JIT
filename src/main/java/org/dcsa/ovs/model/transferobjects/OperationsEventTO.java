package org.dcsa.ovs.model.transferobjects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.ovs.model.base.AbstractOperationsEvent;

@Data
@EqualsAndHashCode(callSuper = true)
public class OperationsEventTO extends AbstractOperationsEvent {

    private PartyTO publisher;
}
