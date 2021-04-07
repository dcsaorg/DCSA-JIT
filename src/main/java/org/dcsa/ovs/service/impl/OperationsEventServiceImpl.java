package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.OperationsEvent;
import org.dcsa.ovs.repository.OperationsEventRepository;
import org.dcsa.ovs.service.OperationsEventService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OperationsEventServiceImpl extends ExtendedBaseServiceImpl<OperationsEventRepository, OperationsEvent, UUID> implements OperationsEventService {

    private final OperationsEventRepository operationsEventRepository;

    @Override
    public OperationsEventRepository getRepository() {
        return operationsEventRepository;
    }
}
