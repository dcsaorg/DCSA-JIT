package org.dcsa.ovs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.ScheduleSubscription;
import org.dcsa.ovs.model.TransportCallSubscription;
import org.dcsa.ovs.service.TransportCallSubscriptionService;
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
@RequestMapping(value = "transport-call-subscriptions", produces = {MediaType.APPLICATION_JSON_VALUE})
@Tag(name = "Transport Call Subscriptions", description = "The Transport Call subscription API")
public class TransportCallSubscriptionController extends ExtendedBaseController<TransportCallSubscriptionService, TransportCallSubscription, UUID> {

    private final TransportCallSubscriptionService transportCallSubscriptionService;

    @Override
    public TransportCallSubscriptionService getService() {
        return transportCallSubscriptionService;
    }

    @Override
    public String getType() {
        return "TransportCallSubscription";
    }

    @Operation(summary = "Find all Transport Call Subscriptions", description = "Finds all Transport Call Subscriptions in the database", tags = { "Subscription" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ScheduleSubscription.class))))
    })
    @GetMapping
    @Override
    public Flux<TransportCallSubscription> findAll(ServerHttpResponse response, ServerHttpRequest request) {
        return super.findAll(response, request);
    }

    @Operation(summary = "Create a Transport Call Subscriptions", description = "Creates a Transport Call Subscriptions in the database", tags = { "Subscription" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransportCallSubscription.class))))
    })
    @PostMapping( consumes = "application/json", produces = "application/json")
    @Override
    public Mono<TransportCallSubscription> create(@Valid @RequestBody TransportCallSubscription transportCallSubscription) {
        return super.create(transportCallSubscription);
    }



}
