package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.BaseController;
import org.dcsa.core.exception.GetException;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.service.TransportCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "schedules/{scheduleID}/transport-calls", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ScheduleTransportCallController extends BaseController<TransportCallService, TransportCall, UUID> {

    private final TransportCallService transportCallService;
    @Autowired
    private final ExtendedParameters extendedParameters;

    @Override
    public String getType() {
        return "TransportCall";
    }

    @Override
    public TransportCallService getService() {
        return transportCallService;
    }

    @GetMapping()
    public Flux<TransportCall> findAll(@PathVariable UUID scheduleID, ServerHttpResponse response, ServerHttpRequest request) {
        ExtendedRequest<TransportCall> extendedRequest = new ExtendedRequest<TransportCall>(extendedParameters, getService().getModelClass());
        try {
            Map<String,String> params = request.getQueryParams().toSingleValueMap();
            params.put("scheduleIDx",scheduleID.toString());
            extendedRequest.parseParameter(params);
        } catch (GetException getException) {
            return Flux.error(getException);
        }
        return getService().findAllExtended(extendedRequest).doOnComplete(
                () -> {
                    // Add Link headers to the response
                    extendedRequest.insertHeaders(response, request);
                }
        );
    }
}
