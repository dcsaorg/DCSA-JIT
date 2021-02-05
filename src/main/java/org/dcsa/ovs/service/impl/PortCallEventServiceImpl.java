package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.PortCallEvent;
import org.dcsa.ovs.repository.PortCallEventRepository;
import org.dcsa.ovs.service.PortCallEventService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PortCallEventServiceImpl extends ExtendedBaseServiceImpl<PortCallEventRepository, PortCallEvent, UUID> implements PortCallEventService {

    private final PortCallEventRepository portCallEventRepository;


    @Override
    public Class<PortCallEvent> getModelClass() {
        return PortCallEvent.class;
    }

    @Override
    public PortCallEventRepository getRepository() {
        return portCallEventRepository;
    }
}
