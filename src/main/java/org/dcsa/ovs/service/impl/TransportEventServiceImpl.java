package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.TransportEvent;
import org.dcsa.ovs.repository.TransportEventRepository;
import org.dcsa.ovs.service.TransportCallService;
import org.dcsa.ovs.service.TransportEventService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransportEventServiceImpl extends ExtendedBaseServiceImpl<TransportEventRepository, TransportEvent, UUID> implements TransportEventService {

    private final TransportEventRepository transportEventRepository;
    private final TransportCallService transportCallService;

    @Override
    public TransportEventRepository getRepository() {
        return transportEventRepository;
    }


    @Override
    public Flux<TransportEvent> mapTransportCall(Flux<TransportEvent> transportEvents){
        return transportEvents
                .flatMap(transportEvent ->
                        transportCallService.findById(transportEvent.getTransportCallID())
                                .doOnNext(transportEvent::setTransportCall)
                                .thenReturn(transportEvent));
    }
}
