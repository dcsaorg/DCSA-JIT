package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.service.TransportCallService;
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
public class TransportCallController extends ExtendedBaseController<TransportCallService, TransportCall, UUID> {

    private final TransportCallService transportCallService;


    @Override
    public String getType() {
        return "TransportCall";
    }

    @Override
    public TransportCallService getService() {
        return transportCallService;
    }

    @GetMapping
    @Override
    public Flux<TransportCall> findAll(ServerHttpResponse response, ServerHttpRequest request) {
        return super.findAll(response, request);
    }

    @GetMapping(value="{id}", produces = "application/json")
    @Override
    public Mono<TransportCall> findById(@PathVariable UUID id) {
        return super.findById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Override
    public Mono<TransportCall> create(@Valid @RequestBody TransportCall transportCall) {
        return super.create(transportCall);
    }

}
