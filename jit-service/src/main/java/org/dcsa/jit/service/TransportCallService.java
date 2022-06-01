package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.persistence.entity.enums.DCSATransportType;
import org.dcsa.jit.persistence.repository.TransportCallRepository;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransportCallService {
  private final TransportCallRepository repository;

  public TransportCall ensureExists(String UNLocationCode, String facilitySMDGCode, DCSATransportType modeOfTransport, String vesselIMONumber, String carrierServiceCode, String importVoyageNumber, String exportVoyageNumber, Integer transportCallSequenceNumber) {

    return null;
  }

  private Optional<TransportCall> findTransportCall(String UNLocationCode, String facilitySMDGCode, DCSATransportType modeOfTransport, String vesselIMONumber, String carrierServiceCode, String importVoyageNumber, String exportVoyageNumber, Integer transportCallSequenceNumber) {
    List<TransportCall> transportCalls = repository.findAllTransportCall(UNLocationCode, facilitySMDGCode, modeOfTransport, vesselIMONumber, carrierServiceCode, importVoyageNumber, exportVoyageNumber, transportCallSequenceNumber);
    if (transportCalls.isEmpty()) {
      return Optional.empty();
    }
    if (transportCalls.size() > 1) {
      if (carrierServiceCode == null) {
        throw transportCallSequenceNumber == null
          ? ConcreteRequestErrorMessageException.invalidInput("Ambiguous transport call; please define sequence number or/and carrier service code + voyage number")
          : ConcreteRequestErrorMessageException.invalidInput("Ambiguous transport call; please define carrier service code and voyage number");
      } else {
        throw transportCallSequenceNumber == null
          ? ConcreteRequestErrorMessageException.invalidInput("Ambiguous transport call; please define sequence number")
          : ConcreteRequestErrorMessageException.internalServerError("Internal error: Ambitious transport call; the result should be unique but is not");
      }
    } else {
      return Optional.of(transportCalls.get(0));
    }
  }
}
