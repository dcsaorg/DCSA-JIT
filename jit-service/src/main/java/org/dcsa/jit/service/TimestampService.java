package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.mapping.LocationMapper;
import org.dcsa.jit.mapping.PartyMapper;
import org.dcsa.jit.mapping.VesselMapper;
import org.dcsa.jit.persistence.entity.*;
import org.dcsa.jit.persistence.entity.enums.PortCallPhaseTypeCode;
import org.dcsa.jit.persistence.repository.*;
import org.dcsa.jit.transferobjects.LocationTO;
import org.dcsa.jit.transferobjects.PartyTO;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.dcsa.jit.transferobjects.VesselTO;
import org.dcsa.jit.transferobjects.enums.FacilityCodeListProvider;
import org.dcsa.jit.transferobjects.enums.ModeOfTransport;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class TimestampService {

  private final EnumMappers enumMappers;
  private final PartyMapper partyMapper;
  private final VesselMapper vesselMapper;
  private final TransportCallService transportCallService;
  private final VesselRepository vesselRepository;
  private final OperationsEventRepository operationsEventRepository;
  private final LocationMapper locationMapper;
  private final TimestampDefinitionService timestampDefinitionService;
  private final UnLocationRepository unLocationRepository;
  private final LocationRepository locationRepository;
  private final UnmappedEventRepository unmappedEventRepository;
  private final PartyRepository partyRepository;
  private final AddressRepository addressRepository;

  @Transactional
  public void create(TimestampTO timestamp) {
    TimestampTO.TimestampTOBuilder timestampTOBuilder = timestamp.toBuilder();
    LocationTO locationTO = timestamp.eventLocation();
    String facilitySMDGCode = timestamp.facilitySMDGCode();
    if (timestamp.modeOfTransport() == null) {
      // JIT IFS says that Mode Of Transport must be omitted for some timestamps
      // and must be VESSEL for others.
      // Because the distinction is not visible after the timestamp has been created, so we cannot
      // rely on it in general either way.
      timestampTOBuilder.modeOfTransport(ModeOfTransport.VESSEL);
    } else if (!timestamp.modeOfTransport().equals(ModeOfTransport.VESSEL)) {
      throw ConcreteRequestErrorMessageException.invalidInput(
          "modeOfTransport must be blank or \"VESSEL\"");
    }

    if (locationTO != null) {
      if (locationTO.UNLocationCode() != null
          && !locationTO.UNLocationCode().equals(timestamp.UNLocationCode())) {
        throw ConcreteRequestErrorMessageException.invalidInput(
            "Conflicting UNLocationCode between the timestamp and the event location");
      }

      if (locationTO.facilityCode() == null ^ locationTO.facilityCodeListProvider() == null) {
        if (locationTO.facilityCode() == null) {
          throw ConcreteRequestErrorMessageException.invalidInput(
              "Cannot create location where facility code list provider is present but facility code is missing");
        }
        throw ConcreteRequestErrorMessageException.invalidInput(
            "Cannot create location where facility code is present but facility code list provider is missing");
      }

      if (locationTO.facilityCodeListProvider() == FacilityCodeListProvider.BIC) {
        // It is complex enough without having to deal with BIC codes, so exclude BIC as an
        // implementation detail.
        throw ConcreteRequestErrorMessageException.invalidInput(
            "The reference implementation only includes supports SMDG codes for facilities.");
      }
      // Ensure timestamp.facilitySMDGCode is set if location.facilityCode is also set
      // The errors for misaligned values are handled below.
      if (timestamp.facilitySMDGCode() == null
          && locationTO.facilityCodeListProvider() == FacilityCodeListProvider.SMDG) {
        timestampTOBuilder.facilitySMDGCode(locationTO.facilityCode());
        facilitySMDGCode = locationTO.facilityCode();
      }
    }
    if (facilitySMDGCode != null) {
      if (locationTO == null) {
        locationTO = LocationTO.builder().build();
      }
      if (locationTO.facilityCodeListProvider() != null
          && locationTO.facilityCodeListProvider() != FacilityCodeListProvider.SMDG) {
        throw ConcreteRequestErrorMessageException.invalidInput(
            "Conflicting facilityCodeListProvider definition (got a facilitySMDGCode but location had a facility with provider: "
                + locationTO.facilityCodeListProvider()
                + ")");
      }
      if (locationTO.facilityCode() != null
          && !locationTO.facilityCode().equals(facilitySMDGCode)) {
        throw ConcreteRequestErrorMessageException.invalidInput(
            "Conflicting facilityCode definition (got a facilitySMDGCode but location had a facility code with a different value provider)");
      }
      locationTO =
          locationTO.toBuilder()
              .UNLocationCode(timestamp.UNLocationCode())
              // We need the UNLocationCode to resolve the facility.
              .facilityCodeListProvider(FacilityCodeListProvider.SMDG)
              .facilityCode(facilitySMDGCode)
              .build();
    }
    timestampTOBuilder.eventLocation(locationTO);
    timestamp = timestampTOBuilder.build();

    this.ensureValidUnLocationCode(
        (timestamp.eventLocation()) == null ? null : timestamp.eventLocation().UNLocationCode());
    this.ensureValidUnLocationCode(timestamp.UNLocationCode());

    TransportCall tc = transportCallService.ensureTransportCallExists(timestamp);

    // Manually handle some entities because JPA cannot do it for us (might be easier if
    // everything used UUID as PK or other IDs that JPA knows how to handle out of the box)
    Location location = saveLocationIfNotNull(timestamp.eventLocation());
    Location vesselPos = saveLocationIfNotNull(timestamp.vesselPosition());
    Party party = savePublisher(timestamp.publisher());
    saveVesselIfNotNull(timestamp.vessel());

    OperationsEvent operationsEvent =
        OperationsEvent.builder()
            .eventClassifierCode(
                enumMappers.eventClassifierCodetoDao(timestamp.eventClassifierCode()))
            .eventDateTime(timestamp.eventDateTime())
            .operationsEventTypeCode(
                enumMappers.operationsEventTypeCodeFromDao(timestamp.operationsEventTypeCode()))
            .portCallPhaseTypeCode(
                enumMappers.portCallPhaseTypeCodeCodetoDao(timestamp.portCallPhaseTypeCode()))
            .portCallServiceTypeCode(
                enumMappers.portCallServiceTypeCodeToDao(timestamp.portCallServiceTypeCode()))
            .publisherRole(enumMappers.publisherRoleToDao(timestamp.publisherRole()))
            .facilityTypeCode(enumMappers.facilityTypeCodeOPRToDao(timestamp.facilityTypeCode()))
            .remark(timestamp.remark())
            .eventLocation(location)
            .vesselPosition(vesselPos)
            .publisher(party)
            .transportCall(tc)
            .vesselDraft(timestamp.vessel() != null ? timestamp.vessel().vesselDraft() : null)
            .milesRemainingToDestination(timestamp.milesRemainingToDestination())
            .build();

    create(operationsEvent);
  }

  private Vessel saveVesselIfNotNull(VesselTO vesselTO) {
    return saveIfNotNull(
      vesselTO,
      vTO -> {
        Optional<Vessel> optionalVessel = vesselRepository.findByVesselIMONumber(vesselTO.vesselIMONumber());
        if (optionalVessel.isPresent()) {
          return optionalVessel.get();
        }
        Vessel vessel = vesselMapper.toEntity(vTO);
        return vesselRepository.save(vessel);
      });
  }

  private Party savePublisher(PartyTO partyTO) {
    // While the method does support partyTO being null, we do not advertise it as
    // the publisher is not null according to the swagger spec.  Calling it "IfNotNull"
    // would send mixed signals.
    return saveIfNotNull(
        partyTO,
        pTO -> {
          Party party = partyMapper.toDao(pTO);
          return partyRepository.save(
              party.toBuilder()
                  .id(UUID.randomUUID().toString())
                  .address(saveIfNotNull(party.getAddress(), addressRepository::save))
                  .build());
        });
  }

  private Location saveLocationIfNotNull(LocationTO locationTO) {
    return saveIfNotNull(
        locationTO,
        lTO -> {
          Location l = locationMapper.toDao(lTO);
          return locationRepository.save(
              l.toBuilder()
                  .id(UUID.randomUUID().toString())
                  .address(saveIfNotNull(l.getAddress(), addressRepository::save))
                  .build());
        });
  }

  private static <T, D> D saveIfNotNull(T entity, Function<T, D> saver) {
    if (entity != null) {
      return saver.apply(entity);
    }
    return null;
  }

  public void create(OperationsEvent operationsEvent) {

    try {
      this.ensurePhaseTypeIsDefined(operationsEvent);
    } catch (IllegalStateException e) {
      throw ConcreteRequestErrorMessageException.invalidInput(
          "Cannot derive portCallPhaseTypeCode automatically from this timestamp. Please define it explicitly");
    }

    operationsEvent = operationsEventRepository.save(operationsEvent);
    timestampDefinitionService.markOperationsEventAsTimestamp(operationsEvent);

    UnmappedEvent unmappedEvent =
        UnmappedEvent.builder()
            .eventID(operationsEvent.getEventID())
            .enqueuedAtDateTime(operationsEvent.getEventDateTime())
            .newRecord(true)
            .build();
    unmappedEventRepository.save(unmappedEvent);
  }

  private void ensurePhaseTypeIsDefined(OperationsEvent oe) {
    if (oe.getPortCallPhaseTypeCode() != null) return;

    PortCallPhaseTypeCode phaseTypeCode =
        timestampDefinitionService.findPhaseTypeCodeFromOperationsEventForJit1_0(oe);
    if (phaseTypeCode != null) {
      oe.setPortCallPhaseTypeCode(phaseTypeCode);
      return;
    }
    throw ConcreteRequestErrorMessageException.invalidParameter(
        "PortCallPhaseTypeCode cannot be omitted!");
  }

  private void ensureValidUnLocationCode(String unLocationCode) {
    if (unLocationCode != null && unLocationRepository.findById(unLocationCode).isEmpty()) {
      throw ConcreteRequestErrorMessageException.invalidParameter(
          "UNLocation with UNLocationCode "
              + unLocationCode
              + " not part of reference implementation data set");
    }
  }
}
