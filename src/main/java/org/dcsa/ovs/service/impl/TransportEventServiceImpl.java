package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.TransportEvent;
import org.dcsa.ovs.repository.TransportEventRepository;
import org.dcsa.ovs.service.TransportEventService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransportEventServiceImpl extends ExtendedBaseServiceImpl<TransportEventRepository, TransportEvent, UUID> implements TransportEventService {

    private final TransportEventRepository transportEventRepository;

    @Override
    public TransportEventRepository getRepository() {
        return transportEventRepository;
    }
}
