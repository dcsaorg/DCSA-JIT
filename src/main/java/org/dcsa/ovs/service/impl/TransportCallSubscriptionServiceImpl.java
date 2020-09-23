package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.TransportCallSubscription;
import org.dcsa.ovs.repository.TransportCallSubscriptionRepository;
import org.dcsa.ovs.service.TransportCallSubscriptionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransportCallSubscriptionServiceImpl extends ExtendedBaseServiceImpl<TransportCallSubscriptionRepository, TransportCallSubscription, UUID> implements TransportCallSubscriptionService {
    private final TransportCallSubscriptionRepository transportCallSubscriptionRepository;

    @Override
    public TransportCallSubscriptionRepository getRepository() {
        return transportCallSubscriptionRepository;
    }

    @Override
    public Class<TransportCallSubscription> getModelClass() {
        return TransportCallSubscription.class;
    }
}
