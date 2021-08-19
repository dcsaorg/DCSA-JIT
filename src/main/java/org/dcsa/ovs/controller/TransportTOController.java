package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.ovs.model.transferobjects.TransportTO;
import org.dcsa.ovs.service.TransportTOService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "unofficial-transports", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TransportTOController extends ExtendedBaseController<TransportTOService, TransportTO, UUID> {

    private final TransportTOService transportTOService;

    @Override
    public String getType() {
        return "Transport";
    }

    @Override
    public TransportTOService getService() {
        return transportTOService;
    }

    @Override
    protected ExtendedRequest<TransportTO> newExtendedRequest() {
        return transportTOService.newExtendedRequest();
    }
}
