package org.dcsa.ovs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.OperationsEvent;
import org.dcsa.ovs.service.OperationsEventService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "transport-calls/operations-events", produces = {MediaType.APPLICATION_JSON_VALUE})
@Tag(name = "Operations Events", description = "The Operation Event API")
public class OperationsEventController extends ExtendedBaseController<OperationsEventService, OperationsEvent, UUID> {

    private final OperationsEventService operationsEventService;

    @Override
    public OperationsEventService getService() {
        return operationsEventService;
    }

    @Override
    public String getType() {
        return "OperationsEvent";
    }


    @Operation(summary = "Save a Operations Event", description = "Saves a Operations Event", tags = { "Operations Event" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation")
    })

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Override
    public Mono<OperationsEvent> create(@Valid @RequestBody OperationsEvent operationsEvent) {
        return super.create(operationsEvent);
    }


}
