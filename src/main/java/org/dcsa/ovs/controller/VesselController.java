package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.events.model.Vessel;
import org.dcsa.core.events.service.VesselService;
import org.dcsa.core.exception.CreateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "vessels", produces = {MediaType.APPLICATION_JSON_VALUE})
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
        if (vessel.getVesselIMONumber() == null) {
            throw new CreateException("Missing vessel IMO number");
        }
        return vesselService.create(vessel);
    }
}
