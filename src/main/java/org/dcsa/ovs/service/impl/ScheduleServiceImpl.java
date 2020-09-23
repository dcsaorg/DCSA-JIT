package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.BaseServiceImpl;
import org.dcsa.ovs.model.Schedule;
import org.dcsa.ovs.repository.ScheduleRepository;
import org.dcsa.ovs.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ScheduleServiceImpl extends BaseServiceImpl<ScheduleRepository, Schedule, UUID> implements ScheduleService {
    private final ScheduleRepository scheduleRepository;


    @Override
    public ScheduleRepository getRepository() {
        return scheduleRepository;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
