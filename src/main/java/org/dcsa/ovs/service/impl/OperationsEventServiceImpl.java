package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.OperationsEvent;
import org.dcsa.ovs.repository.OperationsEventRepository;
import org.dcsa.ovs.service.OperationsEventService;
import org.dcsa.ovs.service.TransportCallService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OperationsEventServiceImpl extends ExtendedBaseServiceImpl<OperationsEventRepository, OperationsEvent, UUID> implements OperationsEventService {

    private final OperationsEventRepository operationsEventRepository;
    private final TransportCallService transportCallService;

    @Override
    public OperationsEventRepository getRepository() {
        return operationsEventRepository;
    }

    @Override
    public Flux<OperationsEvent> mapTransportCall(Flux<OperationsEvent> operationsEvents) {
        return operationsEvents
                .flatMap(operationsEvent -> {
                    return transportCallService.findByUUID(operationsEvent.getTransportCallID())
                            .map(transportCall -> {
                                operationsEvent.setTransportCall(transportCall);
                                return operationsEvent;
                            });
                });
    }

    @Override
    public Mono<OperationsEvent> create(OperationsEvent operationsEvent){
        operationsEvent.setTransportCallID(operationsEvent.getTransportCall().getId());
        return super.save(operationsEvent);


    }
}
