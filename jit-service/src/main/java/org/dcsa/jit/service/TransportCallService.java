package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.persistence.entity.Service;
import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.persistence.entity.Voyage;
import org.dcsa.jit.persistence.entity.enums.FacilityTypeCodeTRN;
import org.dcsa.jit.persistence.repository.TransportCallRepository;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.dcsa.jit.transferobjects.enums.ModeOfTransport;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class TransportCallService {
  private final TransportCallRepository transportCallRepository;
  private final VesselService vesselService;
  private final ServiceService serviceService;
  private final EnumMappers enumMappers;

  @Transactional
  public TransportCall ensureTransportCallExists(TimestampTO timestampTO, Location location) {
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

    Vessel vessel =  vesselService.ensureVesselExistsByImoNumber(timestampTO.vesselIMONumber());

    TimestampTO resolvedTimestampTO = timestampTO;
    return findTransportCall(timestampTO)
      .orElseGet(() -> createTransportCall(resolvedTimestampTO,vessel, location));
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
    return checkForAmbiguity(transportCalls, timestampTO.carrierServiceCode(), timestampTO.transportCallSequenceNumber());
  }

  private Optional<TransportCall> findPortVisit(TransportCall transportCall) {
    // ENSURE THIS METHOD IS ALIGNED WITH ensureTransportCallIsLinkedToPortVisit
    // (which creates the actual portVisit variant)
    String carrierServiceCode = Optional.ofNullable(transportCall.getExportVoyage().getService())
      .map(Service::getCarrierServiceCode)
      .orElse(null);
    List<TransportCall> transportCalls = transportCallRepository.findAllTransportCall(
      transportCall.getLocation().getUNLocationCode(),
      null,  // We select the variant without a facility as the port visit.
      ModeOfTransport.VESSEL.name(),  // This should come from the transport call, but since JIT only works on vessels
                                      // we can simplify a lot by hard coding it (TC has it in the internal format, the
                                      // query expects the enum variant).
      transportCall.getVessel().getVesselIMONumber(),
      carrierServiceCode,
      transportCall.getImportVoyage().getCarrierVoyageNumber(),
      transportCall.getExportVoyage().getCarrierVoyageNumber(),
      transportCall.getTransportCallSequenceNumber(),
      transportCall.getPortVisitReference()
    );

    return checkForAmbiguity(transportCalls, carrierServiceCode, transportCall.getTransportCallSequenceNumber());
  }

  private Optional<TransportCall> checkForAmbiguity(List<TransportCall> transportCalls, String carrierServiceCode, Integer transportCallSequenceNumber) {
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

  private void ensureTransportCallIsLinkedToPortVisit(TransportCall transportCall) {
    UUID portVisitID;
    Optional<TransportCall> portVisit = findPortVisit(transportCall);
    if (portVisit.isPresent()) {
      portVisitID = portVisit.get().getId();
    } else {
      TransportCall newPortVisit = transportCall.toBuilder()
        .id(null)
        .location(Location.builder()
          .UNLocationCode(transportCall.getLocation().getUNLocationCode())
          .build())
        .build();

      transportCallRepository.save(newPortVisit);
      portVisitID = newPortVisit.getId();
      // Create the self-link immediately - makes it easier to reason about what happens.
      // Also, we rely on the self link for the "jit_port_visit" VIEW in the database.
      transportCallRepository.linkPortVisitWithTransportCall(portVisitID, portVisitID);
    }
    transportCallRepository.linkPortVisitWithTransportCall(portVisitID, transportCall.getId());
  }

  private TransportCall createTransportCall(TimestampTO timestampTO, Vessel vessel, Location location) {

    Service service =
      serviceService.ensureServiceExistsByCarrierServiceCode(timestampTO.carrierServiceCode());

      TransportCall entityToSave = TransportCall.builder()
      .transportCallReference(UUID.randomUUID().toString())
      .transportCallSequenceNumber(Objects.requireNonNullElse(timestampTO.transportCallSequenceNumber(), 1))
      .facilityTypeCode(FacilityTypeCodeTRN.POTE) // TODO: this is set as default for now.
      .location(location)
      .modeOfTransportCode(enumMappers.modeOfTransportToDao(timestampTO.modeOfTransport()).getCode().toString())
      .vessel(vessel)
      .importVoyage(Voyage.builder().carrierVoyageNumber(timestampTO.carrierImportVoyageNumber()).service(service).build())
      .exportVoyage(Voyage.builder().carrierVoyageNumber(timestampTO.carrierExportVoyageNumber()).service(service).build())
      .portCallStatusCode(null)
      .portVisitReference(timestampTO.portVisitReference())
      .build();

    return this.create(entityToSave);
  }

  public TransportCall create(TransportCall transportCall) {
    TransportCall savedTransportCall = transportCallRepository.save(transportCall);
    ensureTransportCallIsLinkedToPortVisit(savedTransportCall);
    return savedTransportCall;
  }
}
