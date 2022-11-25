package org.dcsa.jit.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;
import org.dcsa.jit.persistence.entity.enums.OperationsEventTypeCode;
import org.dcsa.jit.service.OperationsEventService;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.dcsa.skernel.infrastructure.http.queryparams.DCSAQueryParameterParser;
import org.dcsa.skernel.infrastructure.http.queryparams.ParsedQueryParameter;
import org.dcsa.skernel.infrastructure.pagination.Cursor;
import org.dcsa.skernel.infrastructure.pagination.CursorDefaults;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.dcsa.skernel.infrastructure.pagination.Paginator;
import org.dcsa.skernel.infrastructure.validation.ValidVesselIMONumber;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.dcsa.skernel.infrastructure.util.EnumUtil.toEnumList;

@Validated
@RestController
@RequiredArgsConstructor
public class EventController {

  private final OperationsEventService eventService;

  private final DCSAQueryParameterParser queryParameterParser;

  private final Paginator paginator;

  @Transactional
  @GetMapping(path = "/events")
  @ResponseStatus(HttpStatus.OK)
  public List<OperationsEventTO> findAll(
      // transportCallReference
      @Size(max = 100) @RequestParam(required = false) String transportCallID,
      @ValidVesselIMONumber(allowNull = true) @RequestParam(required = false)
          String vesselIMONumber,
      @Deprecated @Size(max = 50) @RequestParam(required = false) String carrierVoyageNumber,
      @Size(max = 50) @RequestParam(required = false) String carrierExportVoyageNumber,
      @Size(max = 50) @RequestParam(required = false) String exportVoyageNumber,
      @Size(max = 5) @RequestParam(required = false) String carrierServiceCode,
      @Size(max = 5) @RequestParam(value = "UNLocationCode", required = false)
          String unLocationCode,
      @Size(max = 6) @RequestParam(required = false) String facilitySMDGCode,
      @RequestParam(required = false) String eventClassifierCode,
      @RequestParam(required = false) String operationsEventTypeCode,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false, defaultValue = "100") Integer limit,
      @RequestParam(required = false) String cursor,
      @RequestParam(value = "API-Version", required = false) String apiVersion,
      @RequestParam Map<String, String> queryParams,
      HttpServletRequest request,
      HttpServletResponse response) {

    List<ParsedQueryParameter<OffsetDateTime>> parsedQueryParams =
        queryParameterParser.parseCustomQueryParameter(
            queryParams, "eventCreatedDateTime", OffsetDateTime::parse);

    OperationsEventService.OperationsEventFilters requestFilters =
        OperationsEventService.OperationsEventFilters.builder()
            .transportCallID(transportCallID)
            .vesselIMONumber(vesselIMONumber)
            .carrierVoyageNumber(carrierVoyageNumber)
            .carrierExportVoyageNumber(exportVoyageNumber)
            .exportVoyageNumber(exportVoyageNumber)
            .carrierExportVoyageNumber(carrierExportVoyageNumber)
            .carrierServiceCode(carrierServiceCode)
            .unLocationCode(unLocationCode)
            .facilitySMDGCode(facilitySMDGCode)
            .operationsEventTypeCodes(toEnumList(operationsEventTypeCode, OperationsEventTypeCode.class))
            .eventClassifierCodes(toEnumList(eventClassifierCode, EventClassifierCode.class))
            .eventCreatedDateTime(parsedQueryParams)
            .sort(sort)
            .limit(limit)
            .apiVersion(apiVersion)
            .build();

    Cursor c =
        paginator.parseRequest(
            request,
            new CursorDefaults(limit, new Cursor.SortBy(Sort.Direction.DESC, "eventCreatedDateTime")));

    PagedResult<OperationsEventTO> result = eventService.findAll(requestFilters, c);
    paginator.setPageHeaders(request, response, c, result);
    return result.content();
  }
}
