package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.service.TransportEventService;
import org.dcsa.core.events.service.impl.GenericEventServiceImpl;
import org.dcsa.core.exception.NotFoundException;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.ovs.service.OVSEventService;
import org.dcsa.ovs.service.OperationsEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OVSEventServiceImpl extends GenericEventServiceImpl implements OVSEventService {

    private final TransportEventService transportEventService;
    private final OperationsEventService operationsEventService;

    @Override
    public Flux<Event> findAllExtended(ExtendedRequest<Event> extendedRequest) {
        return super.findAllExtended(extendedRequest).concatMap(event -> {
            switch (event.getEventType()) {
                case TRANSPORT:
                    return transportEventService.loadRelatedEntities((TransportEvent) event);
                case OPERATIONS:
                    return operationsEventService.loadRelatedEntities((OperationsEvent) event);
                default:
                    return Mono.empty();
            }
        });
    }

    @Override
    public Mono<Event> findById(UUID id) {
        return Mono.<Event>empty()
                .switchIfEmpty(transportEventService.findById(id).cast(Event.class))
                .switchIfEmpty(operationsEventService.findById(id).cast(Event.class))
                .switchIfEmpty(Mono.error(new NotFoundException("No event was found with id: " + id)));
    }

    @Override
    public Mono<Event> create(Event event) {
        switch (event.getEventType()) {
            case TRANSPORT:
                return transportEventService.create((TransportEvent) event).cast(Event.class);
            case OPERATIONS:
                return operationsEventService.create((OperationsEvent) event).cast(Event.class);
            default:
                return Mono.error(new IllegalStateException("Unexpected value: " + event.getEventType()));
        }
    }
}
