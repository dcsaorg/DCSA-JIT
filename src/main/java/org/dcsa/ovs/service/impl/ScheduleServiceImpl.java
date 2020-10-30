package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.Schedule;
import org.dcsa.ovs.repository.ScheduleRepository;
import org.dcsa.ovs.repository.ScheduleSubscriptionRepository;
import org.dcsa.ovs.service.ScheduleService;
import org.dcsa.ovs.util.ScheduleCallbackHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ScheduleServiceImpl extends ExtendedBaseServiceImpl<ScheduleRepository, Schedule, UUID> implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleSubscriptionRepository scheduleSubscriptionRepository;

    @Override
    public ScheduleRepository getRepository() {
        return scheduleRepository;
    }

    @Override
    public Class<Schedule> getModelClass() {
        return Schedule.class;
    }

    @Override
    public Mono<Schedule> create(Schedule schedule) {
        return super.save(schedule).doOnNext(
                e -> new ScheduleCallbackHandler(
                        scheduleSubscriptionRepository.getCallbackUrlsByFilters(), e)
                        .start()
        ).map(e -> e);
    }
}
