package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.mapping.LocationMapper;
import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.persistence.entity.Voyage;
import org.dcsa.jit.persistence.repository.FacilityRepository;
import org.dcsa.jit.persistence.repository.LocationRepository;
import org.dcsa.jit.persistence.repository.ServiceRepository;
import org.dcsa.jit.persistence.repository.TransportCallRepository;
import org.dcsa.jit.persistence.repository.VesselRepository;
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
  private final VesselRepository vesselRepository;
  private final ServiceRepository serviceRepository;
  private final EnumMappers enumMappers;

  @Transactional
  public TransportCall ensureVesselExists(TimestampTO timestampTO) {
    if (timestampTO.modeOfTransport() == null) {
      throw ConcreteRequestErrorMessageException.invalidInput("modeOfTransport must be given");
    }

    if ((timestampTO.exportVoyageNumber() != null || timestampTO.importVoyageNumber() != null || timestampTO.carrierServiceCode() != null)
      && (timestampTO.exportVoyageNumber() == null || timestampTO.importVoyageNumber() == null || timestampTO.carrierServiceCode() == null)) {
      throw ConcreteRequestErrorMessageException.invalidInput(
        "exportVoyageNumber, importVoyageNumber and carrierServiceCode must be given together or not at all"
      );
    }

    return findTransportCall(timestampTO)
      .orElseGet(() -> createTransportCall(timestampTO));
  }

  private Optional<TransportCall> findTransportCall(TimestampTO timestampTO) {
    List<TransportCall> transportCalls = transportCallRepository.findAllTransportCall(
      timestampTO.unLocationCode(),
      timestampTO.facilitySMDGCode(), timestampTO.facilitySMDGCode(),
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
    if (timestampTO.carrierServiceCode() == null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Cannot create timestamp where service code is missing");
    }
    if (timestampTO.exportVoyageNumber() == null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Cannot create timestamp where voyage number (carrierVoyageNumber OR exportVoyageNumber + importVoyageNumber) is missing");
    }

    Location location = locationRepository.save(
      Location.builder()
        .id(UUID.randomUUID().toString())
        .unLocationCode(timestampTO.unLocationCode())
        .facility(findFacility(timestampTO))
        .build()
    );
    org.dcsa.jit.persistence.entity.Service service = serviceRepository.save(
      org.dcsa.jit.persistence.entity.Service.builder()
        .carrierServiceCode(timestampTO.carrierServiceCode())
        .build()
    );

    TransportCall entityToSave = TransportCall.builder()
      .reference(UUID.randomUUID().toString())
      .sequenceNumber(Objects.requireNonNullElse(timestampTO.transportCallSequenceNumber(), 1))
      .facility(null) // Go through location to find facility
      .facilityTypeCode(enumMappers.facilityTypeCodeToDao(timestampTO.facilityTypeCode()))
      .location(location)
      .modeOfTransportCode(enumMappers.modeOfTransportToDao(timestampTO.modeOfTransport()).getCode().toString())
      .vessel(ensureVesselExists(timestampTO.vesselIMONumber()))
      .importVoyage(Voyage.builder().carrierVoyageNumber(timestampTO.importVoyageNumber()).service(service).build())
      .exportVoyage(Voyage.builder().carrierVoyageNumber(timestampTO.exportVoyageNumber()).service(service).build())
      .portCallStatusCode(null)
      .build();

    return transportCallRepository.save(entityToSave);
  }

  private Facility findFacility(TimestampTO timestampTO) {
    if (timestampTO.facilitySMDGCode() != null) {
      return facilityRepository.findByUnLocationCodeAndSmdgCode(timestampTO.unLocationCode(), timestampTO.facilitySMDGCode()).orElse(null);
    }
    return null;
  }

  private Vessel ensureVesselExists(String imoNumber) {
    return vesselRepository.findByImoNumber(imoNumber)
      .orElseGet(() -> vesselRepository.save(
        Vessel.builder()
          .imoNumber(imoNumber)
          .isDummy(false)
          .build()
      ));
  }
}
