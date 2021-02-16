package org.dcsa.ovs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.BaseController;
import org.dcsa.core.exception.GetException;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.ovs.model.TransportEvent;
import org.dcsa.ovs.service.transportEventService;
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
@RequestMapping(value = "transport-calls/{transportCallID}/transport-events", produces = {MediaType.APPLICATION_JSON_VALUE})
@Tag(name = "Transport Events", description = "The Transport Event API")
public class TransportCallPortCallEventController extends BaseController<transportEventService, TransportEvent, UUID> {

    private final transportEventService transportEventService;
    @Autowired
    private final ExtendedParameters extendedParameters;


    @Override
    public transportEventService getService() {
        return transportEventService;
    }

    @Override
    public String getType() {
        return "TransportEvent";
    }

    @Operation(summary = "Find all Transport Events", description = "Find all Transport Events in the database", tags = {"Transport Events"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransportEvent.class))))
    })
    @GetMapping()
    public Flux<TransportEvent> findAll(@PathVariable UUID transportCallID, ServerHttpResponse response, ServerHttpRequest request){
        ExtendedRequest<TransportEvent> extendedRequest = new ExtendedRequest<TransportEvent>(extendedParameters, getService().getModelClass());
        try {
            Map<String, String> params = request.getQueryParams().toSingleValueMap();
            params.put("transportCallID", transportCallID.toString());
            extendedRequest.parseParameter(params);
        } catch (GetException getException){
            return Flux.error(getException);
        }
        return getService().findAllExtended(extendedRequest).doOnComplete(
                () -> {
                    extendedRequest.insertHeaders(response, request);
                }
        );
    }
}
