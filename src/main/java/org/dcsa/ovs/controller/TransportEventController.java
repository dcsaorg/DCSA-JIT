package org.dcsa.ovs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.TransportEvent;
import org.dcsa.ovs.service.TransportEventService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "transport-calls/transport-events", produces = {MediaType.APPLICATION_JSON_VALUE})
@Tag(name = "Transport Events", description = "The Transport Event API")
public class TransportEventController extends ExtendedBaseController<TransportEventService, TransportEvent, UUID> {

    private final TransportEventService transportEventService;

    @Override
    public TransportEventService getService() {
        return transportEventService;
    }

    @Override
    public String getType() {
        return "TransportEvent";
    }


    @Operation(summary = "Save a Transport Event", description = "Saves a Transport Event", tags = { "Transport Event" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation")
    })

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Override
    public Mono<TransportEvent> create(@Valid @RequestBody TransportEvent transportEvent) {
        return super.create(transportEvent);
    }


}
