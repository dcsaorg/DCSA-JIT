package org.dcsa.jit.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.mapping.OperationsEventMapper;
import org.dcsa.jit.persistence.repository.OperationsEventRepository;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationsEventService {

  private final OperationsEventRepository eventRepository;

  private final OperationsEventMapper operationsEventMapper;

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
    String eventCreatedDateTime;
    String sort;
    Integer limit;
    String cursor;
    String apiVersion;
  }

  public List<OperationsEventTO> findAll(OperationsEventFilters operationsEventFilters) {
    return eventRepository.findAll().stream().map(operationsEventMapper::toTO).toList();
  }
}
