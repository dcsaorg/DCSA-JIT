package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.NotificationEndpoint;
import org.dcsa.ovs.service.NotificationEndpointService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "unofficial/notification-endpoints", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class NotificationEndpointController extends ExtendedBaseController<NotificationEndpointService, NotificationEndpoint, UUID> {

    private final NotificationEndpointService notificationEndpointService;

    @Override
    public NotificationEndpointService getService() {
        return notificationEndpointService;
    }

    @RequestMapping(
            path = "receive/{id}",
            method = {
                    RequestMethod.POST,
                    RequestMethod.HEAD
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> receivePayload(ServerHttpRequest request, @PathVariable("id") UUID endpointID) {
        return notificationEndpointService.receiveNotification(request, endpointID);
    }
}
