package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.enums.SignatureMethod;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.exception.GetException;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.NotificationEndpoint;
import org.dcsa.ovs.repository.NotificationEndpointRepository;
import org.dcsa.ovs.service.NotificationEndpointService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NotificationEndpointServiceImpl extends ExtendedBaseServiceImpl<NotificationEndpointRepository, NotificationEndpoint, UUID> implements NotificationEndpointService {

    private final NotificationEndpointRepository notificationEndpointRepository;

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
}
