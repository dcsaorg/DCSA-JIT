package org.dcsa.ovs.service;

import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.ovs.model.transferobjects.OperationsEventTO;
import org.dcsa.core.service.BaseService;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface OperationsEventTOService extends BaseService<OperationsEventTO, UUID> {

    Flux <OperationsEventTO> findAllExtended(ExtendedRequest<OperationsEvent> extendedRequest);

}
