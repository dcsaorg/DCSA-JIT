package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.Schedule;
import org.dcsa.ovs.service.ScheduleService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "schedules", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ScheduleController extends ExtendedBaseController<ScheduleService, Schedule, UUID> {

    private final ScheduleService scheduleService;

    @Override
    public String getType() {
        return "schedule";
    }

    @Override
    public ScheduleService getService() {
        return scheduleService;
    }

}
