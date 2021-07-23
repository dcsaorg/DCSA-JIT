package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.events.controller.AbstractEventController;
import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.util.ExtendedGenericEventRequest;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.ovs.service.OVSEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "events", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TransportEventController extends AbstractEventController<OVSEventService, Event> {

    private final OVSEventService ovsEventService;

    @Override
    public OVSEventService getService() {
        return ovsEventService;
    }

    @Override
    public String getType() {
        return "Event";
    }

    @Override
    protected ExtendedRequest<Event> newExtendedRequest() {
        return new ExtendedGenericEventRequest(extendedParameters, r2dbcDialect);
    }

    @Override
    public Mono<Event> create(@Valid @RequestBody Event event) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }
}
