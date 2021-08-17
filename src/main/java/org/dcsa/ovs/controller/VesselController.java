package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.events.model.Vessel;
import org.dcsa.core.events.service.VesselService;
import org.dcsa.core.exception.CreateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "unofficial-vessels", produces = {MediaType.APPLICATION_JSON_VALUE})
public class VesselController extends ExtendedBaseController<VesselService, Vessel, String> {

    private final VesselService vesselService;

    @Override
    public VesselService getService() {
        return vesselService;
    }

    @Override
    @GetMapping(value = "{vesselIMONumber}")
    @ResponseStatus(HttpStatus.OK)
        public Mono<Vessel> findById(@PathVariable String vesselIMONumber) {
        return getService().findById(vesselIMONumber);
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

    @PutMapping( path = "{vesselIMONumber}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Vessel> update(@PathVariable String vesselIMONumber, @Valid @RequestBody Vessel vessel)  {
        if (!vesselIMONumber.equals(vesselService.getIdOfEntity(vessel))) {
            return updateMonoError();
        }
        return vesselService.update(vessel);
    }

    @Override
    @DeleteMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> delete(@RequestBody Vessel vessel) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping( path = "{vesselIMONumber}")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> deleteById(@PathVariable String vesselIMONumber) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

}
