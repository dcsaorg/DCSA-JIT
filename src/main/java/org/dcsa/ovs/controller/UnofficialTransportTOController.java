package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.ovs.model.transferobjects.UnofficialTransportTO;
import org.dcsa.ovs.service.UnofficialTransportTOService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "unofficial-transports", produces = {MediaType.APPLICATION_JSON_VALUE})
public class UnofficialTransportTOController extends ExtendedBaseController<UnofficialTransportTOService, UnofficialTransportTO, UUID> {

    private final UnofficialTransportTOService transportTOService;

    @Override
    public String getType() {
        return "Transport";
    }

    @Override
    public UnofficialTransportTOService getService() {
        return transportTOService;
    }

    @Override
    protected ExtendedRequest<UnofficialTransportTO> newExtendedRequest() {
        return transportTOService.newExtendedRequest();
    }
}
