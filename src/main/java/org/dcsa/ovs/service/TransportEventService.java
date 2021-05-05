package org.dcsa.ovs.service;

import org.dcsa.core.service.ExtendedBaseService;
import org.dcsa.ovs.model.OperationsEvent;
import org.dcsa.ovs.model.TransportEvent;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TransportEventService extends ExtendedBaseService<TransportEvent, UUID> {
    Flux<TransportEvent> mapTransportCall(Flux<TransportEvent> transportEvents);

}