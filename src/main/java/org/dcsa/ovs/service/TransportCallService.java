package org.dcsa.ovs.service;

import org.dcsa.core.service.ExtendedBaseService;
import org.dcsa.ovs.model.TransportCall;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransportCallService extends ExtendedBaseService<TransportCall, UUID> {

    Mono<TransportCall> findByUUID(UUID id);
    Flux<TransportCall> findAll(ServerHttpResponse response, ServerHttpRequest request);
}
