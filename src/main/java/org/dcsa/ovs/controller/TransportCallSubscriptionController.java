package org.dcsa.ovs.controller;

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

}
