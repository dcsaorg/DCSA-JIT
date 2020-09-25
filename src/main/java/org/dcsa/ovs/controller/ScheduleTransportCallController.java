package org.dcsa.ovs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.BaseController;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.exception.GetException;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.ExtendedBaseService;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.service.TransportCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "schedules/{scheduleID}/transport-calls", produces = {MediaType.APPLICATION_JSON_VALUE})
@Tag(name = "Transport Calls", description = "The Transport Call API")
public class ScheduleTransportCallController extends BaseController<TransportCallService, TransportCall, UUID> {

    private final TransportCallService transportCallService;
    @Autowired
    private ExtendedParameters extendedParameters;


    @Override
    public String getType() {
        return "TransportCall";
    }

    @Override
    public TransportCallService getService() {
        return transportCallService;
    }

    @Operation(summary = "Find all Transport Calls", description = "Finds all Transport Calls in the database", tags = {"Transport Call"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransportCall.class))))
    })

//    @GetMapping
//    public Flux<TransportCall> findAllx(@PathVariable UUID scheduleID, ServerHttpResponse response, ServerHttpRequest request) {
//        return super.findAll(response, request);
//    }
    @GetMapping()
    public Flux<TransportCall> findAll(@PathVariable UUID scheduleID,ServerHttpResponse response, ServerHttpRequest request) {
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
