package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.BaseService;
import org.dcsa.core.service.impl.BaseServiceImpl;
import org.dcsa.ovs.model.ScheduleSubscription;
import org.dcsa.ovs.repository.ScheduleSubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ScheduleSubscriptionServiceImpl extends BaseServiceImpl<ScheduleSubscriptionRepository, ScheduleSubscription, UUID> implements BaseService<ScheduleSubscription, UUID> {
    private final ScheduleSubscriptionRepository scheduleSubscriptionRepository;

    @Override
    public ScheduleSubscriptionRepository getRepository() {
        return scheduleSubscriptionRepository;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }

}
