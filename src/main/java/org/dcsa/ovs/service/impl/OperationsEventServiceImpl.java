package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.EquipmentEvent;
import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.service.TransportCallService;
import org.dcsa.core.events.service.TransportCallTOService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.OperationsEvent;
import org.dcsa.ovs.repository.OperationsEventRepository;
import org.dcsa.ovs.service.OperationsEventService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OperationsEventServiceImpl extends ExtendedBaseServiceImpl<OperationsEventRepository, OperationsEvent, UUID> implements OperationsEventService {

    private final OperationsEventRepository operationsEventRepository;
    private final TransportCallTOService transportCallTOService;

    @Override
    public OperationsEventRepository getRepository() {
        return operationsEventRepository;
    }

    private Mono<OperationsEvent> mapTransportCall(OperationsEvent equipmentEvent){
        return transportCallTOService
                .findById(equipmentEvent.getTransportCallID())
                .doOnNext(equipmentEvent::setTransportCall)
                .thenReturn(equipmentEvent);
    }

    @Override
    public Mono<OperationsEvent> loadRelatedEntities(OperationsEvent event) {
        return mapTransportCall(event);
    }

    @Override
    public Mono<OperationsEvent> create(OperationsEvent operationsEvent){
        operationsEvent.setTransportCallID(operationsEvent.getTransportCall().getTransportCallID());
        return super.save(operationsEvent);


    }
}
