package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.*;
import org.dcsa.ovs.repository.FacilityRepository;
import org.dcsa.ovs.repository.TransportCallRepository;
import org.dcsa.ovs.repository.TransportRepository;
import org.dcsa.ovs.service.OperationsEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "transport-calls/operations-events", produces = {MediaType.APPLICATION_JSON_VALUE})
public class OperationsEventController extends ExtendedBaseController<OperationsEventService, OperationsEvent, UUID> {

    private final OperationsEventService operationsEventService;
    private final FacilityRepository facilityRepository;
    private final TransportRepository transportRepository;
    private final TransportCallRepository transportCallRepository;

    @Override
    public OperationsEventService getService() {
        return operationsEventService;
    }

    @Override
    public String getType() {
        return "OperationsEvent";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OperationsEvent> create(@Valid @RequestBody Timestamp timestamp) {
        // Timestamp model needs to be created and used as parameter for this function

        Flux<Facility> x = facilityRepository.getFacilities(timestamp.getUNLocationCode(), timestamp.getFacilitySMDGCode());
        Flux<Transport> y = transportRepository.getTransports(timestamp.getModeOfTransport(), timestamp.getVesselIMONumber());
        Mono<TransportCall> z = transportCallRepository.getTransportCall(","," ");
        OperationsEvent operationsEvent = new OperationsEvent();



        // Create OperationEvent

        // Use OperationEvent to create itself, I guess...
        return operationsEventService.create(operationsEvent);
    }
}
