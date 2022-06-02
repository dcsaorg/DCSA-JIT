package org.dcsa.jit.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.service.TransportCallService;
import org.dcsa.jit.transferobjects.LocationTO;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.dcsa.jit.transferobjects.enums.ModeOfTransport;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {
  private final TransportCallService transportCallService;

  @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
  public TransportCall doStuff() {
    return transportCallService.ensureExists(TimestampTO.builder()
        .modeOfTransport(ModeOfTransport.BARGE)
        .importVoyageNumber("aasd")
        .exportVoyageNumber("asdasd")
        .carrierServiceCode("TNT1")
        .unLocationCode("DEHAM")
        .eventLocation(LocationTO.builder().build())
        .vesselIMONumber("asdas")
        .build());
  }
}
