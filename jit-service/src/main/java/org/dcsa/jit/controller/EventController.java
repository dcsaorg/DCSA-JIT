package org.dcsa.jit.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.service.OperationsEventService;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.dcsa.skernel.infrastructure.http.queryparams.DCSAQueryParameterParser;
import org.dcsa.skernel.infrastructure.http.queryparams.ParsedQueryParameter;
import org.dcsa.skernel.infrastructure.validation.ValidVesselIMONumber;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
public class EventController {

  private final OperationsEventService eventService;

  private final DCSAQueryParameterParser queryParameterParser;

  @Transactional
  @GetMapping(path = "/events")
  @ResponseStatus(HttpStatus.OK)
  public List<OperationsEventTO> findAll(
      // transportCallReference
      @Size(max = 100) @RequestParam(required = false) String transportCallID,
      @ValidVesselIMONumber(allowNull = true) @RequestParam(required = false)
          String vesselIMONumber,
      @Deprecated @Size(max = 50) @RequestParam(required = false) String carrierVoyageNumber,
      @Size(max = 50) @RequestParam(required = false) String exportVoyageNumber,
      @Size(max = 5) @RequestParam(required = false) String carrierServiceCode,
      @Size(max = 5) @RequestParam(value = "UNLocationCode", required = false) String unLocationCode,
      @Size(max = 5) @RequestParam(required = false) String facilitySMDGCode,
      @Size(max = 5) @RequestParam(required = false) String operationsEventTypeCode,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) Integer limit,
      @RequestParam(required = false) String cursor,
      @RequestParam(value = "API-Version", required = false) String apiVersion,
      @RequestParam Map<String, String> queryParams) {

    List<ParsedQueryParameter<OffsetDateTime>> parsedQueryParams = queryParameterParser.parseCustomQueryParameter(
      queryParams,
      "eventCreatedDateTime",
      OffsetDateTime::parse
    );

    return eventService.findAll(
        OperationsEventService.OperationsEventFilters.builder()
            .transportCallID(transportCallID)
            .vesselIMONumber(vesselIMONumber)
            .carrierVoyageNumber(carrierVoyageNumber)
            .exportVoyageNumber(exportVoyageNumber)
            .carrierServiceCode(carrierServiceCode)
            .unLocationCode(unLocationCode)
            .facilitySMDGCode(facilitySMDGCode)
            .operationsEventTypeCode(operationsEventTypeCode)
            .eventCreatedDateTime(parsedQueryParams)
            .sort(sort)
            .limit(limit)
            .cursor(cursor)
            .apiVersion(apiVersion)
            .build());
  }
}
