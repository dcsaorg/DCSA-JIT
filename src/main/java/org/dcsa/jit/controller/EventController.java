package org.dcsa.jit.controller;

import org.dcsa.core.events.controller.AbstractEventController;
import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.model.enums.OperationsEventTypeCode;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.dcsa.core.events.util.ExtendedGenericEventRequest;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.validator.EnumSubset;
import org.dcsa.core.validator.ValidEnum;
import org.dcsa.jit.service.JITEventService;
import org.dcsa.jit.util.ExtendedOperationsEventRequest;
import org.dcsa.skernel.validator.ValidVesselIMONumber;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class EventController extends AbstractEventController<JITEventService, Event> {

    private final JITEventService JITEventService;

    public EventController(@Qualifier("JITEventServiceImpl") JITEventService JITEventService) {
        this.JITEventService = JITEventService;
    }

    @Override
    public JITEventService getService() {
        return JITEventService;
    }

    @Override
    protected ExtendedRequest<Event> newExtendedRequest() {
        return new ExtendedOperationsEventRequest(extendedParameters, r2dbcDialect) {
            @Override
            public void parseParameter(Map<String, List<String>> params) {
                Map<String, List<String>> p = new HashMap<>(params);
                // Add the eventType parameter (if it is missing) in order to limit the resultset
                // to *only* OPERATIONS events
                p.putIfAbsent("eventType", List.of(EventType.OPERATIONS.name()));
                super.parseParameter(p);
            }
        };
    }

    @GetMapping
    public Flux<Event> findAll(
            @RequestParam(value = "eventType", required = false)
            @EnumSubset(anyOf = { "OPERATIONS"})
                    String eventType,
            @RequestParam(value = "transportEventTypeCode", required = false)
            @ValidEnum(clazz = TransportEventTypeCode.class)
                    String transportEventTypeCode,
            @RequestParam(value = "transportCallID", required = false) @Size(max = 100)
                    String transportCallID,
            @RequestParam(value = "vesselIMONumber", required = false) @ValidVesselIMONumber(allowNull = true)
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
}
