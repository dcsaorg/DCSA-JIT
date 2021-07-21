package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.enums.SignatureMethod;
import org.dcsa.core.events.service.impl.MessageSignatureHandler;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.NotificationEndpoint;
import org.dcsa.ovs.repository.NotificationEndpointRepository;
import org.dcsa.ovs.service.NotificationEndpointService;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NotificationEndpointServiceImpl extends ExtendedBaseServiceImpl<NotificationEndpointRepository, NotificationEndpoint, UUID> implements NotificationEndpointService {

    private final NotificationEndpointRepository notificationEndpointRepository;
    private final MessageSignatureHandler messageSignatureHandler;

    @Override
    protected Mono<NotificationEndpoint> preSaveHook(NotificationEndpoint notificationEndpoint) {
        SignatureMethod method = SignatureMethod.HMAC_SHA256;
        byte[] secret = notificationEndpoint.getSecret();
        if (secret == null) {
            return Mono.error(new CreateException("Missing mandatory secret field"));
        }
        if (secret.length < method.getMinKeyLength()) {
            return Mono.error(new CreateException("length of the secret should be minimum " + method.getMinKeyLength()
                    + " bytes long (was: " + secret.length + ")"));
        }
        if (method.getMaxKeyLength() < secret.length) {
            return Mono.error(new CreateException("length of the secret should be maximum " + method.getMinKeyLength()
                    + " bytes long (was: " + secret.length + ")"));
        }
        return super.preSaveHook(notificationEndpoint);
    }

    @Override
    protected Mono<NotificationEndpoint> preUpdateHook(NotificationEndpoint original, NotificationEndpoint update) {
        if (update.getSecret() == null) {
            update.setSecret(original.getSecret());
        }
        return super.preUpdateHook(original, update);
    }

    @Override
    public NotificationEndpointRepository getRepository() {
        return notificationEndpointRepository;
    }

    @Override
    public Mono<Void> receiveNotification(ServerHttpRequest request, UUID endpointID) {
        return findById(endpointID)
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
