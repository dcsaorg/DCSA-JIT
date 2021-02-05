package org.dcsa.ovs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.PortCallEvent;
import org.dcsa.ovs.service.PortCallEventService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "transport-calls/port-call-events", produces = {MediaType.APPLICATION_JSON_VALUE})
@Tag(name = "Port Call Events", description = "The Port Call Event API")
public class PortCallEventController extends ExtendedBaseController<PortCallEventService, PortCallEvent, UUID> {

    private final PortCallEventService portCallEventService;

    @Override
    public PortCallEventService getService() {
        return portCallEventService;
    }

    @Override
    public String getType() {
        return "PortCallEvent";
    }

    @Operation(summary = "Find Port Call Events by ID", description = "Returns a single Port Call Event", tags = { "Port Call Events" }, parameters = {
            @Parameter(in = ParameterIn.PATH, name = "id", description="Id of the Port Call Event to be obtained. Cannot be empty.", required=true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Port Call Event not found")
    })
    @GetMapping(value="{id}", produces = "application/json")
    @Override
    public Mono<PortCallEvent> findById(@PathVariable UUID id) {
        return super.findById(id);
    }

    @Operation(summary = "Save a Port Call Event", description = "Saves a Port Call Event", tags = { "Port Call Events" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation")
    })

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Override
    public Mono<PortCallEvent> create(@Valid @RequestBody PortCallEvent portCallEvent) {
        return super.create(portCallEvent);
    }


}
