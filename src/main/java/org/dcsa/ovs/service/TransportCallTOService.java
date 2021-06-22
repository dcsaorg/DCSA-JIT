package org.dcsa.ovs.service;

import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.BaseService;
import org.dcsa.core.service.ExtendedBaseService;
import org.dcsa.ovs.model.transferobjects.TransportCallTO;

public interface TransportCallTOService extends ExtendedBaseService<TransportCallTO, String> {
    ExtendedRequest<TransportCallTO> newExtendedRequest();
}
