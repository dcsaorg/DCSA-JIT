package org.dcsa.jit.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.service.TimestampService;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "timestamps", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TimestampController {

  private final TimestampService timestampService;
  private final ObjectMapper objectMapper;

  @PostMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public List<TimestampTO> create(@Valid @RequestBody TimestampTO timestamp) throws Exception {
    // TODO: inverse the mapping so we store the original json payload.
    byte[] timestampPayload = objectMapper.writeValueAsBytes(timestamp);
    timestamp.publisher().adjustIdentifyingCodesIfNmftaIsPresent();
    timestampService.create(timestamp, timestampPayload);
    return Collections.emptyList();
  }
}
