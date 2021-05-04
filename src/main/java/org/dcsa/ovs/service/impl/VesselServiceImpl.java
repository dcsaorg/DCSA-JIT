package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.Vessel;
import org.dcsa.ovs.repository.VesselRepository;
import org.dcsa.ovs.service.VesselService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VesselServiceImpl extends ExtendedBaseServiceImpl<VesselRepository, Vessel, String> implements VesselService {

    private final VesselRepository vesselRepository;

    @Override
    public VesselRepository getRepository() {
        return vesselRepository;
    }
}
