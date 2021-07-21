package org.dcsa.ovs.service;

import org.dcsa.core.service.ExtendedBaseService;
import org.dcsa.ovs.model.NotificationEndpoint;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface NotificationEndpointService extends ExtendedBaseService<NotificationEndpoint, UUID> {

    Mono<Void> receiveNotification(ServerHttpRequest request, UUID endpointID);
}
