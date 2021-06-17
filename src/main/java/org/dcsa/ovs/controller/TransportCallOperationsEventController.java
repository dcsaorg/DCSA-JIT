package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.BaseController;
import org.dcsa.core.exception.GetException;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.ovs.model.OperationsEvent;
import org.dcsa.ovs.service.OperationsEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "transport-calls/{transportCallID}/operations-events", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TransportCallOperationsEventController extends BaseController<OperationsEventService, OperationsEvent, UUID> {

    private final OperationsEventService operationsEventService;
    @Autowired
    private final ExtendedParameters extendedParameters;
    private final R2dbcDialect r2dbcDialect;


    @Override
    public OperationsEventService getService() {
        return operationsEventService;
    }

    @Override
    public String getType() {
        return "OperationsEvent";
    }

    @GetMapping()
    public Flux<OperationsEvent> findAll(@PathVariable UUID transportCallID, ServerHttpResponse response, ServerHttpRequest request){
        ExtendedRequest<OperationsEvent> extendedRequest = new ExtendedRequest<OperationsEvent>(extendedParameters, r2dbcDialect, getService().getModelClass());
        try {
            MultiValueMap<String, String> params =  new LinkedMultiValueMap<>();
            //params.add("transportCallID", transportCallID.toString());

            extendedRequest.parseParameter(params);
        } catch (GetException getException){
            return Flux.error(getException);
        }
        // Map TransportCall into operationsEvent!
        return operationsEventService.mapTransportCall(getService().findAllExtended(extendedRequest).doOnComplete(
                () -> {
                    extendedRequest.insertHeaders(response, request);
                }
        ));

    }
}
