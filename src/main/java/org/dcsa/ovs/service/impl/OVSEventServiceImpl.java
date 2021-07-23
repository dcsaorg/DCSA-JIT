package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.EquipmentEvent;
import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.service.impl.GenericEventServiceImpl;
import org.dcsa.core.exception.NotFoundException;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.ovs.service.OVSEventService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OVSEventServiceImpl extends GenericEventServiceImpl implements OVSEventService {

    @Override
    public Class<Event> getModelClass() {
        return Event.class;
    }

    @Override
    public Flux<Event> findAllExtended(ExtendedRequest<Event> extendedRequest) {
        return super.findAllExtended(extendedRequest).concatMap(event -> {
            switch (event.getEventType()) {
                case TRANSPORT:
                    return transportEventService.loadRelatedEntities((TransportEvent) event);
                case EQUIPMENT:
                    return equipmentEventService.loadRelatedEntities((EquipmentEvent) event);
                default:
                    return Mono.just(event);
            }
        });
    }

    @Override
    public Mono<Event> findById(UUID id) {
        return Mono.<Event>empty()
                .switchIfEmpty(transportEventService.findById(id).cast(Event.class))
                .switchIfEmpty(equipmentEventService.findById(id).cast(Event.class))
                .switchIfEmpty(Mono.error(new NotFoundException("No event was found with id: " + id)));
    }

    @Override
    public Mono<Event> create(Event event) {
        switch (event.getEventType()) {
            case TRANSPORT:
                return transportEventService.create((TransportEvent) event).cast(Event.class);
            case EQUIPMENT:
                return equipmentEventService.create((EquipmentEvent) event).cast(Event.class);
            default:
                return Mono.error(new IllegalStateException("Unexpected value: " + event.getEventType()));
        }
    }
}
