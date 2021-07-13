package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.ovs.service.TransportCallTOService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "transport-calls", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TransportCallTOController extends ExtendedBaseController<TransportCallTOService, TransportCallTO, String> {

    private final TransportCallTOService transportCallService;

    @Override
    public String getType() {
        return "TransportCall";
    }

    @Override
    public TransportCallTOService getService() {
        return transportCallService;
    }

    @Override
    protected ExtendedRequest<TransportCallTO> newExtendedRequest() {
        return transportCallService.newExtendedRequest();
    }
}
