package org.dcsa.ovs.service;

import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.ExtendedBaseService;
import org.dcsa.ovs.model.transferobjects.TransportCallTO;
import org.dcsa.ovs.model.transferobjects.TransportTO;

import java.util.UUID;

public interface TransportTOService extends ExtendedBaseService<TransportTO, UUID> {
    ExtendedRequest<TransportTO> newExtendedRequest();
}
