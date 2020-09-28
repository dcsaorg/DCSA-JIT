package org.dcsa.ovs.service;

import org.dcsa.core.service.ExtendedBaseService;
import org.dcsa.ovs.model.ScheduleSubscription;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ScheduleSubscriptionService extends ExtendedBaseService<ScheduleSubscription, UUID> {


}
