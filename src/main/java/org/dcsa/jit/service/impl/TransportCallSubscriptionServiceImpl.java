package org.dcsa.jit.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.jit.model.TransportCallSubscription;
import org.dcsa.jit.repository.TransportCallSubscriptionRepository;
import org.dcsa.jit.service.TransportCallSubscriptionService;
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

}
