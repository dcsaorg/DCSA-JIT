package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.combined.ExtendedTransportCall;
import org.dcsa.ovs.repository.ExtendedTransportCallRepository;
import org.dcsa.ovs.repository.TransportCallSubscriptionRepository;
import org.dcsa.ovs.service.ExtendedTransportCallService;
import org.dcsa.ovs.service.TransportCallService;
import org.dcsa.ovs.util.TransportCallCallbackHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ExtendedTransportCallServiceImpl extends ExtendedBaseServiceImpl<ExtendedTransportCallRepository, ExtendedTransportCall, UUID> implements ExtendedTransportCallService {
    private final ExtendedTransportCallRepository extendedTransportCallRepository;
    private final TransportCallSubscriptionRepository transportCallSubscriptionRepository;


    @Override
    public ExtendedTransportCallRepository getRepository() {
        return extendedTransportCallRepository;
    }

    @Override
    public Mono<ExtendedTransportCall> create(ExtendedTransportCall extendedTransportCall) {
        extendedTransportCall.setVesselIMONumber(extendedTransportCall.getVesselIMONumber());
        return super.save(extendedTransportCall);
    }

}
