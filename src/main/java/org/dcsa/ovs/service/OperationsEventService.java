package org.dcsa.ovs.service;

import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.service.ExtendedBaseService;
import org.dcsa.ovs.model.OperationsEvent;
import org.dcsa.ovs.model.base.AbstractOperationsEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OperationsEventService extends ExtendedBaseService<OperationsEvent, UUID> {
    Mono<OperationsEvent> loadRelatedEntities(OperationsEvent event);
}