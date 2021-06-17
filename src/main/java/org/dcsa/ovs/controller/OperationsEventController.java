package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.OperationsEvent;
import org.dcsa.ovs.service.OperationsEventService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "transport-calls/operations-events", produces = {MediaType.APPLICATION_JSON_VALUE})
public class OperationsEventController extends ExtendedBaseController<OperationsEventService, OperationsEvent, UUID> {

    private final OperationsEventService operationsEventService;

    @Override
    public OperationsEventService getService() {
        return operationsEventService;
    }

    @Override
    public String getType() {
        return "OperationsEvent";
    }

}
