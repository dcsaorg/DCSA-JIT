package org.dcsa.ovs.service.impl;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.repository.EventRepository;
import org.dcsa.core.events.repository.PendingEventRepository;
import org.dcsa.core.events.service.EquipmentEventService;
import org.dcsa.core.events.service.OperationsEventService;
import org.dcsa.core.events.service.ShipmentEventService;
import org.dcsa.core.events.service.TransportEventService;
import org.dcsa.core.events.service.impl.GenericEventServiceImpl;
import org.dcsa.core.exception.NotFoundException;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.ovs.service.OVSEventService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@Service
public class OVSEventServiceImpl extends GenericEventServiceImpl implements OVSEventService {

    private final Set<EventType> SUPPORTED_EVENT_TYPES = Set.of(EventType.OPERATIONS, EventType.TRANSPORT);

    public OVSEventServiceImpl(
      TransportEventService transportEventService,
      EquipmentEventService equipmentEventService,
      ShipmentEventService shipmentEventService,
      OperationsEventService operationsEventService,
      EventRepository eventRepository,
      PendingEventRepository pendingEventRepository) {
    super(
        shipmentEventService,
        transportEventService,
        equipmentEventService,
        operationsEventService,
        eventRepository,
        pendingEventRepository);
    }

    protected Set<EventType> getSupportedEvents() {
        return SUPPORTED_EVENT_TYPES;
    }

    @Override
    public Flux<Event> findAllExtended(ExtendedRequest<Event> extendedRequest) {
    return super.findAllExtended(extendedRequest)
        .filter(
            e -> SUPPORTED_EVENT_TYPES.contains(e.getEventType()))
        .concatMap(
            event -> {
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
                .switchIfEmpty(getTransportEventRelatedEntities(id))
                .switchIfEmpty(getOperationsEventRelatedEntities(id))
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
