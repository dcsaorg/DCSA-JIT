package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.ScheduleSubscription;
import org.dcsa.ovs.repository.ScheduleSubscriptionRepository;
import org.dcsa.ovs.service.ScheduleSubscriptionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ScheduleSubscriptionServiceImpl extends ExtendedBaseServiceImpl<ScheduleSubscriptionRepository, ScheduleSubscription, UUID> implements ScheduleSubscriptionService {
    private final ScheduleSubscriptionRepository scheduleSubscriptionRepository;

    @Override
    public ScheduleSubscriptionRepository getRepository() {
        return scheduleSubscriptionRepository;
    }

}
