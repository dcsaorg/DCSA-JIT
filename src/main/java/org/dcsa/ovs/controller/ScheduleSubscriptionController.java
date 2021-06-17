package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.ScheduleSubscription;
import org.dcsa.ovs.service.ScheduleSubscriptionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "schedule-subscriptions", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ScheduleSubscriptionController extends ExtendedBaseController<ScheduleSubscriptionService, ScheduleSubscription, UUID> {

    private final ScheduleSubscriptionService scheduleSubscriptionService;

    @Override
    public ScheduleSubscriptionService getService() {
        return scheduleSubscriptionService;
    }

    @Override
    public String getType() {
        return "ScheduleSubscription";
    }

}
