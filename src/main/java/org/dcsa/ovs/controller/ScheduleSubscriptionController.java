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
import org.dcsa.ovs.service.ScheduleSubscriptionService;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "schedule-subscriptions", produces = {MediaType.APPLICATION_JSON_VALUE})
@Tag(name = "Schedule Subscriptions", description = "The Schedule subscription API")
public class ScheduleSubscriptionController extends ExtendedBaseController<ScheduleSubscriptionService, ScheduleSubscription, UUID> {

    private final ScheduleSubscriptionService scheduleSubscriptionService;

    @Override
    public ScheduleSubscriptionService getService() {
        return scheduleSubscriptionService;
    }

    @Override
    public String getType() {
        return "ScheduleSubscription";
    }

    @Operation(summary = "Find all Schedule Subscriptions", description = "Finds all Schedule Subscriptions in the database", tags = { "Events" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ScheduleSubscription.class))))
    })
    @GetMapping
    public Flux<ScheduleSubscription> findAll(ServerHttpResponse response, ServerHttpRequest request) {
        return super.findAll(response, request);
    }

    @Operation(summary = "Create a Schedule Subscription", description = "Create a Schedule Subscription", tags = { "Schedule" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ScheduleSubscription.class))))
    })
    @PostMapping( consumes = "application/json", produces = "application/json")
    public Mono<ScheduleSubscription> save(@RequestBody ScheduleSubscription scheduleSubscription) {
        return super.save(scheduleSubscription);
    }



}
