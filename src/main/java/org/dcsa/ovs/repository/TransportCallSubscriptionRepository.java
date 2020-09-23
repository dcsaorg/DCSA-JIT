package org.dcsa.ovs.repository;

import org.dcsa.ovs.model.TransportCallSubscription;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface TransportCallSubscriptionRepository extends R2dbcRepository<TransportCallSubscription, UUID> {

}
