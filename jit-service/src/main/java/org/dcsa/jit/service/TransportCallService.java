package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.persistence.entity.Voyage;
import org.dcsa.jit.persistence.entity.enums.FacilityTypeCodeTRN;
import org.dcsa.jit.persistence.repository.*;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransportCallService {
  private final TransportCallRepository transportCallRepository;
  private final LocationRepository locationRepository;
  private final FacilityRepository facilityRepository;
  private final VesselService vesselService;
  private final ServiceService serviceService;
  private final EnumMappers enumMappers;

  @Transactional
  public TransportCall ensureTransportCallExists(TimestampTO timestampTO) {

    if ((timestampTO.importVoyageNumber() != null && timestampTO.exportVoyageNumber() != null)
      == (timestampTO.carrierVoyageNumber() != null)) {
      throw ConcreteRequestErrorMessageException.invalidInput(
        "Cannot create timestamp where both voyage numbers carrierVoyageNumber AND (exportVoyageNumber + importVoyageNumber) " +
          "is missing or provided together");
    }

    return findTransportCall(timestampTO)
      .orElseGet(() -> createTransportCall(timestampTO));
  }

  private Optional<TransportCall> findTransportCall(TimestampTO timestampTO) {
    List<TransportCall> transportCalls = transportCallRepository.findAllTransportCall(
      timestampTO.UNLocationCode(),
      timestampTO.facilitySMDGCode(),
      timestampTO.modeOfTransport().name(),
      timestampTO.vesselIMONumber(),
      timestampTO.carrierServiceCode(),
      timestampTO.importVoyageNumber(),
      timestampTO.exportVoyageNumber(),
      timestampTO.transportCallSequenceNumber()
    );

    if (transportCalls.isEmpty()) {
      return Optional.empty();
    }
    if (transportCalls.size() > 1) {
      if (timestampTO.carrierServiceCode() == null) {
        throw timestampTO.transportCallSequenceNumber() == null
          ? ConcreteRequestErrorMessageException.invalidInput("Ambiguous transport call; please define sequence number or/and carrier service code + voyage number")
          : ConcreteRequestErrorMessageException.invalidInput("Ambiguous transport call; please define carrier service code and voyage number");
      } else {
        throw timestampTO.transportCallSequenceNumber() == null
          ? ConcreteRequestErrorMessageException.invalidInput("Ambiguous transport call; please define sequence number")
          : ConcreteRequestErrorMessageException.internalServerError("Internal error: Ambitious transport call; the result should be unique but is not");
      }
    } else {
      return Optional.of(transportCalls.get(0));
    }
  }

  private TransportCall createTransportCall(TimestampTO timestampTO) {

    Location location = locationRepository.save(
      Location.builder()
        .id(UUID.randomUUID().toString())
        .UNLocationCode(timestampTO.UNLocationCode())
        .facility(findFacility(timestampTO))
        .build()
    );
    org.dcsa.jit.persistence.entity.Service service =
      serviceService.ensureServiceExistsByCarrierServiceCode(timestampTO.carrierServiceCode());

    TransportCall entityToSave = TransportCall.builder()
      .transportCallReference(UUID.randomUUID().toString())
      .transportCallSequenceNumber(Objects.requireNonNullElse(timestampTO.transportCallSequenceNumber(), 1))
      .facility(null) // Go through location to find facility,
      .facilityTypeCode(FacilityTypeCodeTRN.POTE) // TODO: this is set as default for now.
      .location(location)
      .modeOfTransportCode(enumMappers.modeOfTransportToDao(timestampTO.modeOfTransport()).getCode().toString())
      .vessel(vesselService.ensureVesselExistsByImoNumber(timestampTO.vesselIMONumber()))
      .importVoyage(Voyage.builder().carrierVoyageNumber(timestampTO.importVoyageNumber()).service(service).build())
      .exportVoyage(Voyage.builder().carrierVoyageNumber(timestampTO.exportVoyageNumber()).service(service).build())
      .portCallStatusCode(null)
      .build();

    return transportCallRepository.save(entityToSave);
  }

  private Facility findFacility(TimestampTO timestampTO) {
    if (timestampTO.facilitySMDGCode() != null) {
      return facilityRepository.findByUNLocationCodeAndFacilitySMDGCode(timestampTO.UNLocationCode(), timestampTO.facilitySMDGCode()).orElse(null);
    }
    return null;
  }
}
