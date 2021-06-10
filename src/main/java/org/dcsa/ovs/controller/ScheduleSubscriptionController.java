package org.dcsa.ovs.controller;

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

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "schedule-subscriptions", produces = {MediaType.APPLICATION_JSON_VALUE})
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

    @GetMapping
    @Override
    public Flux<ScheduleSubscription> findAll(ServerHttpResponse response, ServerHttpRequest request) {
        return super.findAll(response, request);
    }

    @PostMapping( consumes = "application/json", produces = "application/json")
    @Override
    public Mono<ScheduleSubscription> create(@Valid @RequestBody ScheduleSubscription scheduleSubscription) {
        return super.create(scheduleSubscription);
    }

}
