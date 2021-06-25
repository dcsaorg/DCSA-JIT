package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.ovs.model.transferobjects.TransportTO;
import org.dcsa.ovs.service.TransportTOService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "transports", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TransportTOController extends ExtendedBaseController<TransportTOService, TransportTO, UUID> {

    private final TransportTOService transportCallService;

    @Override
    public String getType() {
        return "Transport";
    }

    @Override
    public TransportTOService getService() {
        return transportCallService;
    }

    @Override
    protected ExtendedRequest<TransportTO> newExtendedRequest() {
        return transportCallService.newExtendedRequest();
    }
}
