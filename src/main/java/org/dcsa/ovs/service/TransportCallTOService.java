package org.dcsa.ovs.service;

import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.ExtendedBaseService;

public interface TransportCallTOService extends ExtendedBaseService<TransportCallTO, String> {
    ExtendedRequest<TransportCallTO> newExtendedRequest();
}
