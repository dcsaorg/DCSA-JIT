package org.dcsa.jit.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.service.OperationsEventService;
import org.dcsa.skernel.infrastructure.validation.ValidVesselIMONumber;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class EventController {

  private final OperationsEventService eventService;

  @GetMapping(path = "/events")
  @ResponseStatus(HttpStatus.OK)
  public List<OperationsEvent> findAll(
      @Size(max = 100) @RequestParam(required = false) String transportCallID,
      @Size(max = 7) @ValidVesselIMONumber(allowNull = true) @RequestParam(required = false) String vesselIMONumber,
      @Deprecated @Size(max = 50) @RequestParam(required = false) String carrierVoyageNumber,
      @Size(max = 50) @RequestParam(required = false) String exportVoyageNumber,
      @Size(max = 5) @RequestParam(required = false) String carrierServiceCode,
      @Size(max = 5) @RequestParam(value = "UNLocationCode", required = false)
          String unLocationCode,
      @Size(max = 5) @RequestParam(required = false) String operationsEventTypeCode,
      @RequestParam(required = false) String eventCreatedDateTime,
      @RequestParam(required = false) Integer limit,
      @RequestParam(required = false) String cursor,
      @RequestParam(value = "API-Version", required = false) String apiVersion) {
    return eventService.findAll();
  }
}
