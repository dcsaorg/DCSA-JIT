package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.TransportEvent;
import org.dcsa.ovs.service.transportEventService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "transport-calls/transport-events", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TransportEventController extends ExtendedBaseController<transportEventService, TransportEvent, UUID> {

    private final transportEventService transportEventService;

    @Override
    public transportEventService getService() {
        return transportEventService;
    }

    @Override
    public String getType() {
        return "TransportEvent";
    }

    @GetMapping(value="{id}", produces = "application/json")
    @Override
    public Mono<TransportEvent> findById(@PathVariable UUID id) {
        return super.findById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Override
    public Mono<TransportEvent> create(@Valid @RequestBody TransportEvent transportEvent) {
        return super.create(transportEvent);
    }

}
