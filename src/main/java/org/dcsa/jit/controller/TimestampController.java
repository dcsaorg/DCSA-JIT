package org.dcsa.jit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.dcsa.jit.model.Timestamp;
import org.dcsa.jit.service.TimestampService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "timestamps", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TimestampController {

    private final TimestampService timestampService;
    private final ObjectMapper objectMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Timestamp> create(@Valid @RequestBody Timestamp timestamp) throws Exception {
        // TODO: inverse the mapping so we store the original json payload.
        byte[] timestampPayload = objectMapper.writeValueAsBytes(timestamp);
        timestamp.getPublisher().adjustIdentifyingCodesIfNmftaIsPresent();
        return timestampService.create(timestamp, timestampPayload).flatMap(x -> Mono.empty());
    }
}
