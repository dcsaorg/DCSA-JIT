package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.BaseController;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.exception.GetException;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.ovs.model.transferobjects.TransportCallTO;
import org.dcsa.ovs.service.TransportCallTOService;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
