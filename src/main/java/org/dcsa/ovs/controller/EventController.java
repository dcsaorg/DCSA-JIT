package org.dcsa.ovs.controller;

import org.dcsa.core.events.controller.AbstractEventController;
import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.model.enums.OperationsEventTypeCode;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.dcsa.core.events.util.ExtendedGenericEventRequest;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.validator.EnumSubset;
import org.dcsa.core.validator.ValidEnum;
import org.dcsa.ovs.service.OVSEventService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping(value = "events", produces = {MediaType.APPLICATION_JSON_VALUE})
public class EventController extends AbstractEventController<OVSEventService, Event> {

    private final OVSEventService ovsEventService;

    public EventController(@Qualifier("OVSEventServiceImpl") OVSEventService ovsEventService) {
        this.ovsEventService = ovsEventService;
    }

    @Override
    public OVSEventService getService() {
        return ovsEventService;
    }

    @Override
    public String getType() {
        return "Event";
    }

    @Override
    protected ExtendedRequest<Event> newExtendedRequest() {
        return new ExtendedGenericEventRequest(extendedParameters, r2dbcDialect) {
            @Override
            public void parseParameter(Map<String, List<String>> params) {
                Map<String, List<String>> p = new HashMap<>(params);
                // Add the eventType parameter (if it is missing) in order to limit the resultset
                // to *only* TRANSPORT and OPERATIONS events
                p.putIfAbsent("eventType", List.of(
                        EventType.TRANSPORT.name() + "," +
                        EventType.OPERATIONS.name()));
                super.parseParameter(p);
            }
        };
    }

    @GetMapping
    public Flux<Event> findAll(
            @RequestParam(value = "eventType", required = false)
            @EnumSubset(anyOf = { "TRANSPORT", "OPERATIONS"})
                    String eventType,
            @RequestParam(value = "transportEventTypeCode", required = false)
            @ValidEnum(clazz = TransportEventTypeCode.class)
                    String transportEventTypeCode,
            @RequestParam(value = "transportCallID", required = false) @Size(max = 100)
                    String transportCallID,
            @RequestParam(value = "vesselIMONumber", required = false) @Size(max = 7)
                    String vesselIMONumber,
            @RequestParam(value = "carrierVoyageNumber", required = false) @Size(max = 50)
                    String carrierVoyageNumber,
            @RequestParam(value = "carrierServiceCode", required = false) @Size(max = 5)
                    String carrierServiceCode,
            @RequestParam(value = "operationsEventTypeCode", required = false)
            @ValidEnum(clazz = OperationsEventTypeCode.class)
                    String operationsEventTypeCode,
            @RequestParam(value = "limit", defaultValue = "1", required = false) @Min(1) int limit,
            ServerHttpResponse response,
            ServerHttpRequest request) {
        return super.findAll(response, request);
    }

    @Override
    public Mono<Event> create(@Valid @RequestBody Event event) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid param value")
    @ExceptionHandler(ConstraintViolationException.class)
    public void badRequest() {}
}