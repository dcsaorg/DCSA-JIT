package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.BaseServiceImpl;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.repository.TransportCallRepository;
import org.dcsa.ovs.service.TransportCallService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransportCallServiceImpl extends BaseServiceImpl<TransportCallRepository, TransportCall, UUID> implements TransportCallService {
    private final TransportCallRepository transportCallRepository;


    @Override
    public TransportCallRepository getRepository() {
        return transportCallRepository;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }

}
