package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.events.service.impl.MessageSignatureHandler;
import org.dcsa.ovs.model.NotificationEndpoint;
import org.dcsa.ovs.service.NotificationEndpointService;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "unofficial/notification-endpoints", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class NotificationEndpointController extends ExtendedBaseController<NotificationEndpointService, NotificationEndpoint, UUID> {

    private final NotificationEndpointService notificationEndpointService;
    private final MessageSignatureHandler messageSignatureHandler;

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
        return notificationEndpointService.findById(endpointID)
                .flatMap(notificationEndpoint -> {
                    String subscriptionID = notificationEndpoint.getSubscriptionID();
                    if (request.getMethod() == HttpMethod.HEAD) {
                        // verify request - we are happy at this point. (note that we forgive missing subscriptionIDs
                        // as the endpoint can be verified before we know the Subscription ID)
                        return Mono.empty();
                    }
                    if (subscriptionID == null) {
                        // We do not have a subscription ID yet. Assume that it is not a match
                        // Ideally, we would include a "Retry-After" header as well.
                        return Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE));
                    }
                    return messageSignatureHandler.verifyRequest(request,
                            notificationEndpoint.getSubscriptionID(),
                            notificationEndpoint.getSecret(),
                            Map.class);
                }).flatMap(signatureResult -> {
                    if (!signatureResult.isValid()) {
                        // The unconditional usage of UNAUTHORIZED is deliberate. We are not interested in letting
                        // the caller know why we are rejecting - just that we are not happy.  Telling more might
                        // inform them of a bug or enable them to guess part of the secret.
                        log.debug("Rejecting message because: " + signatureResult.getResult());
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                    }
                    // TODO: Implement as a part of DDT-114 using signatureResult.getParsed() and return "Mono.empty()"
                    // (or end with .then())
                    // Remember you can change the 4th parameter of "verifyRequest" above to change the type of
                    // value returned by getParsed (it might require changes if you want to support complex
                    // types)
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED));
                }).then();
    }
}
