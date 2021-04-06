package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.OperationsEvent;
import org.dcsa.ovs.repository.TransportEventRepository;
import org.dcsa.ovs.service.operationsEventService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OperationsEventServiceImpl extends ExtendedBaseServiceImpl<TransportEventRepository, OperationsEvent, UUID> implements operationsEventService {

    private final TransportEventRepository portCallEventRepository;

    @Override
    public TransportEventRepository getRepository() {
        return portCallEventRepository;
    }
}
