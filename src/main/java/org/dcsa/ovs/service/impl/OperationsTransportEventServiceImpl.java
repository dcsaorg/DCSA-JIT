package org.dcsa.ovs.service.impl;

import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.repository.TransportEventRepository;
import org.dcsa.core.events.service.TransportCallService;
import org.dcsa.core.events.service.TransportCallTOService;
import org.dcsa.core.events.service.impl.TransportEventServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service("operationsTransportEventServiceImpl")
public class OperationsTransportEventServiceImpl extends TransportEventServiceImpl {
    public OperationsTransportEventServiceImpl(TransportEventRepository transportEventRepository, TransportCallService transportCallService, TransportCallTOService transportCallTOService) {
        super(transportEventRepository, transportCallService, transportCallTOService);
    }

    @Override
    public Mono<TransportEvent> loadRelatedEntities(TransportEvent event) {
        return mapTransportCall(event);
    }

}
