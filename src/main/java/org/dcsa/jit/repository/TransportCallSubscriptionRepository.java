package org.dcsa.jit.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.jit.model.TransportCallSubscription;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TransportCallSubscriptionRepository extends ExtendedRepository<TransportCallSubscription, UUID> {

    @Query("SELECT event_subscription.callback_url"
           + " FROM event_subscription"
           + " JOIN event_subscription_event_types ON (event_subscription.subscription_id = event_subscription_event_types.subscription_id)"
           + " WHERE event_subscription_event_types.event_type = 'TRANSPORT'")
    Flux<String> getCallbackUrlsByFilters();

}
