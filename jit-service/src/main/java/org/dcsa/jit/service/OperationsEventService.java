package org.dcsa.jit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.mapping.OperationsEventMapper;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.repository.OperationsEventRepository;
import org.dcsa.jit.persistence.repository.specification.OperationsEventSpecification;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.dcsa.skernel.infrastructure.http.queryparams.ParsedQueryParameter;
import org.dcsa.skernel.infrastructure.pagination.Cursor;
import org.dcsa.skernel.infrastructure.pagination.CursorDefaults;
import org.dcsa.skernel.infrastructure.pagination.Paginator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.OffsetDateTime;
import java.util.List;

import static org.dcsa.jit.persistence.repository.specification.OperationsEventSpecification.withFilters;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationsEventService {

  private final OperationsEventRepository operationsEventRepository;

  private final OperationsEventMapper operationsEventMapper;

  private final Paginator paginator;

  @Builder
  public static class OperationsEventFilters {
    String transportCallID;
    String vesselIMONumber;
    String carrierVoyageNumber;
    String exportVoyageNumber;
    String carrierServiceCode;
    String unLocationCode;
    String facilitySMDGCode;
    String operationsEventTypeCode;
    List<ParsedQueryParameter<OffsetDateTime>> eventCreatedDateTime;
    String sort;
    Integer limit;
    String cursor;
    String apiVersion;
  }

  public List<OperationsEventTO> findAll(
      HttpServletRequest request,
      HttpServletResponse response,
      final OperationsEventFilters requestFilters) {

    Cursor cursor =
        paginator.parseRequest(
            request,
            new CursorDefaults(
                requestFilters.limit,
                new Cursor.SortBy(Sort.Direction.DESC, "createdDateTime")));

    Page<OperationsEvent> splat =
        operationsEventRepository.findAll(
            withFilters(
                OperationsEventSpecification.OperationsEventFilters.builder()
                    .transportCallID(requestFilters.transportCallID)
                    .vesselIMONumber(requestFilters.vesselIMONumber)
                    .carrierVoyageNumber(requestFilters.carrierVoyageNumber)
                    .exportVoyageNumber(requestFilters.exportVoyageNumber)
                    .carrierServiceCode(requestFilters.carrierServiceCode)
                    .unLocationCode(requestFilters.unLocationCode)
                    .facilitySMDGCode(requestFilters.facilitySMDGCode)
                    .operationsEventTypeCode(requestFilters.operationsEventTypeCode)
                    .eventCreatedDateTime(requestFilters.eventCreatedDateTime)
                    .build()),
            cursor.toPageRequest());

    paginator.setPageHeaders(request, response, cursor, splat.getTotalPages());

    return splat.stream().map(operationsEventMapper::toTO).toList();
  }
}
