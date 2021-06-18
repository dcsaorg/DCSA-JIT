package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.Carrier;
import org.dcsa.ovs.service.CarrierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "carriers", produces = {MediaType.APPLICATION_JSON_VALUE})
public class CarrierController extends ExtendedBaseController<CarrierService, Carrier, UUID> {

    private final CarrierService carrierService;

    @Override
    public CarrierService getService() {
        return carrierService;
    }

    @PostMapping
    @ResponseStatus( HttpStatus.FORBIDDEN )
    public Mono<Carrier> create(@Valid @RequestBody Carrier carrier) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @PutMapping( path = "{carrierID}")
    public Mono<Carrier> update(@PathVariable UUID carrierID, @Valid @RequestBody Carrier carrier) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping
    @ResponseStatus( HttpStatus.FORBIDDEN )
    public Mono<Void> delete(@RequestBody Carrier carrier) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping( path ="{carrierID}" )
    @ResponseStatus( HttpStatus.FORBIDDEN )
    public Mono<Void> deleteById(@PathVariable UUID carrierID) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }
}
