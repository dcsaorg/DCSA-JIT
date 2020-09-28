package org.dcsa.ovs.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.ovs.model.TransportCallSubscription;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TransportCallSubscriptionRepository extends ExtendedRepository<TransportCallSubscription, UUID> {

    @Query("SELECT callback_url FROM dcsa_ovs_v1_0.transport_call_subscription " )
    Flux<String> getCallbackUrlsByFilters();

}
