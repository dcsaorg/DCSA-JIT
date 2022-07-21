package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.persistence.entity.Voyage;
import org.dcsa.jit.persistence.entity.enums.FacilityTypeCodeTRN;
import org.dcsa.jit.persistence.repository.FacilityRepository;
import org.dcsa.jit.persistence.repository.LocationRepository;
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
    // Backwards compatibility with JIT 1.1
    if (timestampTO.importVoyageNumber() != null || timestampTO.exportVoyageNumber() != null) {
      if (timestampTO.carrierImportVoyageNumber() != null || timestampTO.carrierExportVoyageNumber() != null) {
        throw ConcreteRequestErrorMessageException.invalidInput(
          "Cannot mix carrierExportVoyageNumber/carrierImportVoyageNumber (jit 1.2) and importVoyageNumber/exportVoyageNumber (jit 1.1) naming");
      }
      // Put values into the correct place for rest of the code
      timestampTO = timestampTO.toBuilder()
        .carrierImportVoyageNumber(timestampTO.importVoyageNumber())
        .carrierExportVoyageNumber(timestampTO.exportVoyageNumber())
        .importVoyageNumber(null)
        .exportVoyageNumber(null)
        .build();
    }

    if (timestampTO.carrierImportVoyageNumber() != null || timestampTO.carrierExportVoyageNumber() != null) {
      if (timestampTO.carrierImportVoyageNumber() == null || timestampTO.carrierExportVoyageNumber() == null) {
        throw ConcreteRequestErrorMessageException.invalidInput(
            "Both voyage carrierExportVoyageNumber + carrierImportVoyageNumber should either be provided together or not at all");
      }
      if (!(timestampTO.carrierImportVoyageNumber().equals(timestampTO.carrierVoyageNumber())
          || timestampTO.carrierExportVoyageNumber().equals(timestampTO.carrierVoyageNumber()))) {
        throw ConcreteRequestErrorMessageException.invalidInput(
            "When carrierImportVoyageNumber & carrierExportVoyageNumber is given, then one of them has to equal the carrierVoyageNumber."
                + " Please verify the values");
      }
    }

    // Backwards compatibility with JIT 1.1
    // if both carrierExportVoyageNumber & carrierImportVoyageNumber are not given
    // then we set them based on carrierVoyageNumber
    // This is done to persist Voyage as carrierVoyageNumber is mandatory (Line 120)
    if (timestampTO.carrierImportVoyageNumber() == null) {
      timestampTO =
          timestampTO.toBuilder()
              .carrierExportVoyageNumber(timestampTO.carrierVoyageNumber())
              .carrierImportVoyageNumber(timestampTO.carrierVoyageNumber())
              .build();
    }

    TimestampTO resolvedTimestampTO = timestampTO;
    return findTransportCall(timestampTO)
      .orElseGet(() -> createTransportCall(resolvedTimestampTO));
  }

  private Optional<TransportCall> findTransportCall(TimestampTO timestampTO) {
    List<TransportCall> transportCalls = transportCallRepository.findAllTransportCall(
      timestampTO.UNLocationCode(),
      timestampTO.facilitySMDGCode(),
      timestampTO.modeOfTransport().name(),
      timestampTO.vesselIMONumber(),
      timestampTO.carrierServiceCode(),
      timestampTO.carrierImportVoyageNumber(),
      timestampTO.carrierExportVoyageNumber(),
      timestampTO.transportCallSequenceNumber(),
      timestampTO.portVisitReference()
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
      .importVoyage(Voyage.builder().carrierVoyageNumber(timestampTO.carrierImportVoyageNumber()).service(service).build())
      .exportVoyage(Voyage.builder().carrierVoyageNumber(timestampTO.carrierExportVoyageNumber()).service(service).build())
      .portCallStatusCode(null)
      .portVisitReference(timestampTO.portVisitReference())
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
