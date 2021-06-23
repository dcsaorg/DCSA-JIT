package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.exception.CreateException;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.model.Vessel;
import org.dcsa.ovs.service.TransportCallService;
import org.dcsa.ovs.service.VesselService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "unofficial/vessels", produces = {MediaType.APPLICATION_JSON_VALUE})
public class VesselController extends ExtendedBaseController<VesselService, Vessel, String> {

    private final VesselService vesselService;

    @Override
    public VesselService getService() {
        return vesselService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Vessel> create(@Valid @RequestBody Vessel vessel) {
        // Override subclass as we *want* an ID (default is that the ID must be absent)
        if (vessel.getId() == null) {
            throw new CreateException("Missing vessel IMO number");
        }
        return vesselService.create(vessel);
    }
}