package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.service.TransportCallService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "transport-calls", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TransportCallController extends ExtendedBaseController<TransportCallService, TransportCall, UUID> {

    private final TransportCallService transportCallService;

    @Override
    public String getType() {
        return "TransportCall";
    }

    @Override
    public TransportCallService getService() {
        return transportCallService;
    }

}
