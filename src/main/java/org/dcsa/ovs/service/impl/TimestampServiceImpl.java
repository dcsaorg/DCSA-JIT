package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.repository.TransportCallRepository;
import org.dcsa.core.events.service.LocationService;
import org.dcsa.core.events.service.PartyService;
import org.dcsa.core.events.service.VesselPositionService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.BaseServiceImpl;
import org.dcsa.ovs.model.OperationsEvent;
import org.dcsa.ovs.model.Timestamp;
import org.dcsa.ovs.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TimestampServiceImpl extends BaseServiceImpl<Timestamp, UUID> implements TimestampService {

    private final VesselPositionService vesselPositionService;
    private final OperationsEventService operationsEventService;
    private final TransportCallRepository transportCallRepository;
    private final LocationService locationService;
    private final PartyService partyService;

    @Override
    public Flux<Timestamp> findAll() {
        return Flux.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public Mono<Timestamp> findById(UUID id) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public Mono<Timestamp> create(Timestamp timestamp) {
        OperationsEvent operationsEvent = new OperationsEvent();
        operationsEvent.setEventClassifierCode(timestamp.getEventClassifierCode());
        operationsEvent.setEventDateTime(timestamp.getEventDateTime());
        operationsEvent.setOperationsEventTypeCode(timestamp.getOperationsEventTypeCode());

        return transportCallRepository.getTransportCall(timestamp.getUNLocationCode(), timestamp.getFacilitySMDGCode(), timestamp.getModeOfTransport(), timestamp.getVesselIMONumber())
                .switchIfEmpty(Mono.error(new CreateException("No matching TransportCall found!")))
                .flatMap(transportCall -> {
                    operationsEvent.setTransportCallID(transportCall.getTransportCallID());
                    operationsEvent.setTransportCall(transportCall);

                    if (timestamp.getPublisher() == null) {
                        return Mono.error(new CreateException("Party is empty or null"));
                    }

                    return Mono.just(timestamp.getPublisher())
                            .flatMap(partyService::ensureResolvable)
                            .doOnNext(party -> operationsEvent.setPublisherID(party.getId()))
                            .thenReturn(timestamp.getEventLocation());
                })
                .flatMap(location -> Mono.justOrEmpty(location)
                        .flatMap(locationService::ensureResolvable)
                        .thenReturn(timestamp.getVesselPosition())
                ).flatMap(vesselPosition -> Mono.justOrEmpty(vesselPosition)
                        .flatMap(vesselPositionService::ensureResolvable)
                        .thenReturn(operationsEvent)
                ).flatMap(operationsEventService::create)
                .thenReturn(timestamp);
    }

    @Override
    public Mono<Timestamp> update(Timestamp transport) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public Mono<Void> delete(Timestamp transport) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public UUID getIdOfEntity(Timestamp entity) {
        return null;
    }

    @Override
    public Flux<Timestamp> findAllExtended(ExtendedRequest<Timestamp> extendedRequest) {
        return Flux.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }
}
