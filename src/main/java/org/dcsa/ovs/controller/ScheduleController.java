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
import org.dcsa.ovs.model.Schedule;
import org.dcsa.ovs.service.ScheduleService;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "schedules", produces = {MediaType.APPLICATION_JSON_VALUE})
@Tag(name = "Schedules", description = "The schedule API")
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

    @Operation(summary = "Find all Schedules", description = "Finds all Schedules in the database", tags = { "Schedule" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Schedule.class))))
    })
    @GetMapping
    public Flux<Schedule> findAll(ServerHttpResponse response, ServerHttpRequest request) {
        return super.findAll(response, request);
    }

    @Operation(summary = "Find Schedule by ID", description = "Returns a single Schedule", tags = { "Schedule" }, parameters = {
            @Parameter(in = ParameterIn.PATH, name = "id", description="Id of the Schedule to be obtained. Cannot be empty.", required=true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    @GetMapping(value="{id}", produces = "application/json")
    @Override
    public Mono<Schedule> findById(@PathVariable UUID id) {
        return super.findById(id);
    }

    @Operation(summary = "Save a Schedule", description = "Saves a Schedule", tags = { "Schedule" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    @Override
    public Mono<Schedule> save(@RequestBody Schedule schedule) {
        return super.save(schedule);
    }

}
