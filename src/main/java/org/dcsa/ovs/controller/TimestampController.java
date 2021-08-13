package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.Timestamp;
import org.dcsa.ovs.service.TimestampService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "timestamps", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TimestampController extends ExtendedBaseController<TimestampService, Timestamp, UUID> {

    private final TimestampService timestampService;

    @Override
    public TimestampService getService() {
        return timestampService;
    }

    @Override
    public String getType() {
        return "Timestamps";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Timestamp> create(@Valid @RequestBody Timestamp timestamp) {
        return timestampService.create(timestamp).flatMap(x -> Mono.empty());
    }
}
