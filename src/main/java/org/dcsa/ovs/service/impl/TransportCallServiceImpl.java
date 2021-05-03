package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.repository.TransportCallRepository;
import org.dcsa.ovs.repository.TransportCallSubscriptionRepository;
import org.dcsa.ovs.service.TransportCallService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransportCallServiceImpl extends ExtendedBaseServiceImpl<TransportCallRepository, TransportCall, UUID> implements TransportCallService {
    private final TransportCallRepository transportCallRepository;
    private final TransportCallSubscriptionRepository transportCallSubscriptionRepository;


    @Override
    public TransportCallRepository getRepository() {
        return transportCallRepository;
    }



}
