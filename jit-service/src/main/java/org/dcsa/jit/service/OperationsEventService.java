package org.dcsa.jit.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.repository.OperationsEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationsEventService {

  private final OperationsEventRepository eventRepository;

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

  public List<OperationsEvent> findAll(OperationsEventFilters operationsEventFilters) {
    return eventRepository.findAll();
  }
}
