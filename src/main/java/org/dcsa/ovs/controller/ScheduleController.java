package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.Schedule;
import org.dcsa.ovs.service.ScheduleService;
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
@RequestMapping(value = "schedules", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ScheduleController extends ExtendedBaseController<ScheduleService, Schedule, UUID> {

    private final ScheduleService scheduleService;

    @Override
    public String getType() {
        return "schedule";
    }

    @Override
    public ScheduleService getService() {
        return scheduleService;
    }

    @GetMapping
    @Override
    public Flux<Schedule> findAll(ServerHttpResponse response, ServerHttpRequest request) {
        return super.findAll(response, request);
    }

    @GetMapping(value="{id}", produces = "application/json")
    @Override
    public Mono<Schedule> findById(@PathVariable UUID id) {
        return super.findById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Override
    public Mono<Schedule> create(@Valid @RequestBody Schedule schedule) {
        return super.create(schedule);
    }

}
