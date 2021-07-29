package org.dcsa.ovs.model.transferobjects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.transferobjects.PartyTO;

@Data
@EqualsAndHashCode(callSuper = true)
public class OperationsEventTO extends OperationsEvent {

    private PartyTO publisher;
}
