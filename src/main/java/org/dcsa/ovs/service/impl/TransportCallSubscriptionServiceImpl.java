package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.BaseService;
import org.dcsa.core.service.impl.BaseServiceImpl;
import org.dcsa.ovs.model.TransportCallSubscription;
import org.dcsa.ovs.repository.TransportCallSubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransportCallSubscriptionServiceImpl extends BaseServiceImpl<TransportCallSubscriptionRepository, TransportCallSubscription, UUID> implements BaseService<TransportCallSubscription, UUID> {
    private final TransportCallSubscriptionRepository transportCallSubscriptionRepository;

    @Override
    public TransportCallSubscriptionRepository getRepository() {
        return transportCallSubscriptionRepository;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
