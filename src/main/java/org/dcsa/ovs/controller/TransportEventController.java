package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.service.TransportEventService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "events", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TransportEventController extends ExtendedBaseController<TransportEventService, TransportEvent, UUID> {

    private final TransportEventService transportEventService;

    @Override
    public TransportEventService getService() {
        return transportEventService;
    }

    @Override
    public String getType() {
        return "Event";
    }

}
