package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.events.service.TransportCallTOService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "unofficial-TransportCalls", produces = {MediaType.APPLICATION_JSON_VALUE})

public class TransportCallController extends ExtendedBaseController<TransportCallTOService, TransportCallTO, String> {

    private final TransportCallTOService transportCallTOService;

    @Override
    public TransportCallTOService getService() {
        return transportCallTOService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransportCallTO> create(@RequestBody TransportCallTO transportCallTO) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    @PutMapping( path = "{transportCallID}")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<TransportCallTO> update(@PathVariable String transportCallID, @RequestBody TransportCallTO transportCallTO) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    @DeleteMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> delete(@RequestBody TransportCallTO transportCallTO) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping( path = "{transportCallID}")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> deleteById(@PathVariable String transportCallID) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }


}
