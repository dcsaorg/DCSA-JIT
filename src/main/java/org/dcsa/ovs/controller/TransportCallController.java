package org.dcsa.ovs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.service.TransportCallService;
import org.dcsa.ovs.service.VesselService;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "transport-calls", produces = {MediaType.APPLICATION_JSON_VALUE})
@Tag(name = "Transport Calls", description = "The Transport Call API")
public class TransportCallController extends ExtendedBaseController<TransportCallService, TransportCall, UUID> {

    private final TransportCallService transportCallService;
    private final VesselService vesselService;


    @Override
    public String getType() {
        return "TransportCall";
    }

    @Override
    public TransportCallService getService() {
        return transportCallService;
    }

    @Operation(summary = "Find all Transport Calls", description = "Finds all Transport Calls in the database", tags = { "Transport Call" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransportCall.class))))
    })
    @GetMapping
    @Override
    public Flux<TransportCall> findAll(ServerHttpResponse response, ServerHttpRequest request) {
       return transportCallService.findAll(response, request);
    }

    @Operation(summary = "Find Transport Call by ID", description = "Returns a single Transport Call", tags = { "Transport Call" }, parameters = {
            @Parameter(in = ParameterIn.PATH, name = "id", description="Id of the Transport Call to be obtained. Cannot be empty.", required=true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Transport Call not found")
    })
    @GetMapping(value="{id}", produces = "application/json")
    @Override
    public Mono<TransportCall> findById(@PathVariable UUID id) {
        return transportCallService.findByUUID(id);

    }

    @Operation(summary = "Save a Transport Call", description = "Saves a Transpor Call", tags = { "Transport Call" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    @Override
    public Mono<TransportCall> create(@Valid @RequestBody TransportCall transportCall) {
        return super.create(transportCall);
    }

}
