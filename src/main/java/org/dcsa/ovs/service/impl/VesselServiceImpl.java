package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.util.ValidationUtils;
import org.dcsa.ovs.model.Vessel;
import org.dcsa.ovs.repository.VesselRepository;
import org.dcsa.ovs.service.VesselService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class VesselServiceImpl extends ExtendedBaseServiceImpl<VesselRepository, Vessel, String> implements VesselService {

    private final VesselRepository vesselRepository;

    @Override
    public VesselRepository getRepository() {
        return vesselRepository;
    }

    @Override
    public Mono<Vessel> create(Vessel vessel) {
        if (vessel.getId() == null) {
            throw new IllegalArgumentException("Missing vessel IMO number");
        }
        ValidationUtils.validateVesselIMONumber(vessel.getId());
        return preCreateHook(vessel)
                .flatMap(this::preSaveHook)
                .flatMap(vesselRepository::insert);
    }
}
