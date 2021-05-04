package org.dcsa.ovs.service;

import org.dcsa.core.service.ExtendedBaseService;
import org.dcsa.ovs.model.OperationsEvent;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface OperationsEventService extends ExtendedBaseService<OperationsEvent, UUID> {
    Flux<OperationsEvent> findAll(Flux<OperationsEvent> operationsEvents);
}