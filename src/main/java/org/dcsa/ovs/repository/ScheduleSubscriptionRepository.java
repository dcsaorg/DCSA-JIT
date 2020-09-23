package org.dcsa.ovs.repository;

import org.dcsa.ovs.model.ScheduleSubscription;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface ScheduleSubscriptionRepository extends R2dbcRepository<ScheduleSubscription, UUID> {

}
