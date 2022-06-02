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
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransportCallService {
  private final TransportCallRepository repository;
  private final LocationRepository locationRepository;
  private final FacilityRepository facilityRepository;
  private final ServiceRepository serviceRepository;
  private final EnumMappers enumMappers;
  private final LocationMapper locationMapper;

  @Transactional
  public TransportCall ensureExists(TimestampTO timestampTO) {
    validateTimestampTO(timestampTO);
    return findTransportCall(timestampTO)
      .orElseGet(() -> createTransportCall(timestampTO));
  }

  private void validateTimestampTO(TimestampTO timestampTO) {
    if (timestampTO.modeOfTransport() == null) {
      throw ConcreteRequestErrorMessageException.invalidInput("modeOfTransport must be given");
    }
    if (timestampTO.exportVoyageNumber() != null ^ timestampTO.importVoyageNumber() != null) {
      throw ConcreteRequestErrorMessageException.invalidInput("exportVoyageNumber and importVoyageNumber must be given together or not at all");
    }
    if (timestampTO.carrierServiceCode() == null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Cannot create timestamp where service code is missing");
    }
    if (timestampTO.exportVoyageNumber() == null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Cannot create timestamp where voyage number (carrierVoyageNumber OR exportVoyageNumber + importVoyageNumber) is missing");
    }
  }

  private Optional<TransportCall> findTransportCall(TimestampTO timestampTO) {
    List<TransportCall> transportCalls = repository.findAllTransportCall(
      timestampTO.unLocationCode(),
      null, null, // Where do we get
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
    Facility facility = null; // saveIf(timestampTO.unLocationCode(), () -> Facility.builder().unLocationCode(timestampTO.unLocationCode()).build(), facilityRepository::save);
    Location location = saveIf(timestampTO.unLocationCode(), () -> Location.builder().id(UUID.randomUUID().toString()).unLocationCode(timestampTO.unLocationCode()).build(), locationRepository::save);
    org.dcsa.jit.persistence.entity.Service service = saveIf(timestampTO.exportVoyageNumber(), () -> org.dcsa.jit.persistence.entity.Service.builder().carrierServiceCode(timestampTO.carrierServiceCode()).build(), serviceRepository::save);

    TransportCall entityToSave = TransportCall.builder()
      .reference(UUID.randomUUID().toString()) // TODO where is this supposed to come from
      .sequenceNumber(Objects.requireNonNullElse(timestampTO.transportCallSequenceNumber(), 1))
      .facility(facility)
      .facilityTypeCode(enumMappers.facilityTypeCodeToDao(timestampTO.facilityTypeCode()))
      .location(location)
      .modeOfTransportCode(enumMappers.modeOfTransportToDao(timestampTO.modeOfTransport()).getCode().toString())
      .vessel(timestampTO.vesselIMONumber() != null ? Vessel.builder().imoNumber(timestampTO.vesselIMONumber()).isDummy(false).build() : null)
      .importVoyage(timestampTO.importVoyageNumber() != null ? Voyage.builder().carrierVoyageNumber(timestampTO.importVoyageNumber()).build() : null)
      .exportVoyage(timestampTO.exportVoyageNumber() !=null ? Voyage.builder().carrierVoyageNumber(timestampTO.exportVoyageNumber()).service(service).build() : null)
      .portCallStatusCode(null)
      .build();

    return repository.save(entityToSave);
  }

  private <I,T> T saveIf(I identifier, Supplier<T> supplier, Function<T, T> saver) {
    if (identifier != null) {
      T entity = supplier.get();
      log.info("Attempting to save {}", entity);
      entity = saver.apply(entity);
      log.info("Saved {}", entity);
      return entity;
    }
    return null;
  }
}
