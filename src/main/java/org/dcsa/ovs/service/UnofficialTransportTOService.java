package org.dcsa.ovs.service;

import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.ExtendedBaseService;
import org.dcsa.ovs.model.transferobjects.UnofficialTransportTO;

import java.util.UUID;

public interface UnofficialTransportTOService extends ExtendedBaseService<UnofficialTransportTO, UUID> {
    ExtendedRequest<UnofficialTransportTO> newExtendedRequest();
}
