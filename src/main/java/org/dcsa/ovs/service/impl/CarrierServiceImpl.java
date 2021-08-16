package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.events.model.Carrier;
import org.dcsa.core.events.repository.CarrierRepository;
import org.dcsa.ovs.service.CarrierService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CarrierServiceImpl extends ExtendedBaseServiceImpl<CarrierRepository, Carrier, UUID> implements CarrierService {

    private final CarrierRepository carrierRepository;

    @Override
    public CarrierRepository getRepository() {
        return carrierRepository;
    }
}
