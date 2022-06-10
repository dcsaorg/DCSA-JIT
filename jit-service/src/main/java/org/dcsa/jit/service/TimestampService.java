package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.mapping.LocationMapper;
import org.dcsa.jit.mapping.PartyMapper;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.Party;
import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.persistence.entity.UnmappedEvent;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;
import org.dcsa.jit.persistence.entity.enums.OperationsEventTypeCode;
import org.dcsa.jit.persistence.entity.enums.PortCallPhaseTypeCode;
import org.dcsa.jit.persistence.repository.*;
import org.dcsa.jit.transferobjects.LocationTO;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.dcsa.jit.transferobjects.enums.FacilityCodeListProvider;
import org.dcsa.jit.transferobjects.enums.ModeOfTransport;
import org.dcsa.jit.transferobjects.enums.PortCallServiceTypeCode;
import org.dcsa.skernel.domain.persistence.entity.Address;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TimestampService {

  private final EnumMappers enumMappers;
  private final PartyMapper partyMapper;
  private final TransportCallService transportCallService;
  private final OperationsEventRepository operationsEventRepository;
  private final LocationMapper locationMapper;
  private final TimestampDefinitionService timestampDefinitionService;
  private final UnLocationRepository unLocationRepository;
  private final LocationRepository locationRepository;
  private final UnmappedEventRepository unmappedEventRepository;
  private final PartyRepository partyRepository;
  private final AddressRepository addressRepository;

  @Transactional
  public void create(TimestampTO timestamp, byte[] originalPayload) {
    TimestampTO.TimestampTOBuilder timestampTOBuilder = timestamp.toBuilder();
    LocationTO locationTO = timestamp.eventLocation();
    if (timestamp.modeOfTransport() == null) {
      // JIT IFS says that Mode Of Transport must be omitted for some timestamps and must be VESSEL
      // for others.
      // Because the distinction is not visible after the timestamp has been created, so we cannot
      // rely on it
      // in general either way.
      timestampTOBuilder = timestampTOBuilder.modeOfTransport(ModeOfTransport.VESSEL);
    }
    if (!timestamp.modeOfTransport().equals(ModeOfTransport.VESSEL)) {
      throw ConcreteRequestErrorMessageException.invalidInput(
        "modeOfTransport must be blank or \"VESSEL\"");
    }

    if (locationTO != null) {
      if (locationTO.unLocationCode() != null &&
        !locationTO.unLocationCode().equals(timestamp.unLocationCode())) {
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
      // Ensure timestamp.facilitySMDGCode and location.facilityCode is aligned
      // The errors for misaligned values are handled below.
      if (timestamp.facilitySMDGCode() == null &&
        locationTO.facilityCodeListProvider() == FacilityCodeListProvider.SMDG) {
        timestampTOBuilder = timestampTOBuilder.facilitySMDGCode(locationTO.facilityCode());
      }
    }
    if (timestamp.facilitySMDGCode() != null) {
      if (locationTO == null) {
        locationTO = LocationTO.builder().build();

      }
      // We need the UNLocode to resolve the facility.
      locationTO = locationTO.toBuilder().unLocationCode(timestamp.unLocationCode()).build();
      if (locationTO.facilityCodeListProvider() != null &&
        locationTO.facilityCodeListProvider() != FacilityCodeListProvider.SMDG) {
        throw ConcreteRequestErrorMessageException.invalidInput(
          "Conflicting facilityCodeListProvider definition (got a facilitySMDGCode but location had a facility with provider: " +
            locationTO.facilityCodeListProvider() +
            ")");
      }
      if (locationTO.facilityCode() != null &&
        !locationTO.facilityCode().equals(timestamp.facilitySMDGCode())) {
        throw ConcreteRequestErrorMessageException.invalidInput(
          "Conflicting facilityCode definition (got a facilitySMDGCode but location had a facility code with a different value provider)");
      }
      locationTO = locationTO.toBuilder()
        .facilityCodeListProvider(FacilityCodeListProvider.SMDG)
        .facilityCode(timestamp.facilitySMDGCode())
        .build();
      timestampTOBuilder = timestampTOBuilder.eventLocation(locationTO);
    }

    timestamp = timestampTOBuilder.build();

    this.ensureValidUnLocationCode(Objects.requireNonNull(timestamp.eventLocation()).unLocationCode());
    this.ensureValidUnLocationCode(timestamp.unLocationCode());

    TransportCall tc = transportCallService.ensureTransportCallExists(timestamp);

    Party party = partyMapper.toDao(timestamp.publisher());
    Location location = locationMapper.toDao(timestamp.eventLocation());
    Address partyAddress = addressRepository.save(party.getAddress());
    Address locationAddress = addressRepository.save(location.getAddress());
    location = locationRepository.save(location.toBuilder().id(UUID.randomUUID().toString()).address(locationAddress).build());
    party = party.toBuilder().id(UUID.randomUUID().toString()).address(partyAddress).build();
    party = partyRepository.save(party); // persist party entity

    // TODO DDT-10xx: work around to avoid TransientPropertyValueException on vesselPosition -> Address
    Location vesselPos = locationMapper.toDao(timestamp.vesselPosition());
    Address vesselAddress = addressRepository.save(new Address());
    vesselPos = vesselPos.toBuilder().address(vesselAddress).id(UUID.randomUUID().toString()).build();
    vesselPos = locationRepository.save(vesselPos);



    OperationsEvent operationsEvent =
      OperationsEvent.builder()
        .createdDateTime(timestamp.eventDateTime())
        .classifierCode(enumMappers.eventClassifierCodetoDao(timestamp.eventClassifierCode()))
        .dateTime(timestamp.eventDateTime())
        .operationsEventTypeCode(
          enumMappers.operationsEventTypeCodeFromDao(timestamp.operationsEventTypeCode()))
        .portCallPhaseTypeCode(
          enumMappers.portCallPhaseTypeCodeCodetoDao(timestamp.portCallPhaseTypeCode()))
        .portCallServiceTypeCode(
          enumMappers.portCallServiceTypeCodeToDao(timestamp.portCallServiceTypeCode()))
        .publisherRole(enumMappers.publisherRoleToDao(timestamp.publisherRole()))
        .facilityTypeCode(enumMappers.facilityTypeCodeToDao(timestamp.facilityTypeCode()))
        .remark(timestamp.remark())
        .location(location)
        .vesselPosition(vesselPos)
        .publisher(party)
        .transportCall(tc)
        .build();

    create(operationsEvent);
  }

  public void create(OperationsEvent operationsEvent) {

    try {
      this.ensurePhaseTypeIsDefined(
        operationsEvent,
        enumMappers.portCallServiceTypeCodeFromDao(operationsEvent.getPortCallServiceTypeCode()));
    } catch (IllegalStateException e) {
      throw ConcreteRequestErrorMessageException.invalidInput(
        "Cannot derive portCallPhaseTypeCode automatically from this timestamp. Please define it explicitly");
    }

    operationsEvent = operationsEventRepository.save(operationsEvent);
    timestampDefinitionService.markOperationsEventAsTimestamp(operationsEvent);


    UnmappedEvent unmappedEvent =
      UnmappedEvent.builder()
        .eventID(operationsEvent.getId())
        .enqueuedAtDateTime(operationsEvent.getDateTime())
        .newRecord(true)
        .build();
    unmappedEventRepository.save(unmappedEvent);
  }

  // TODO: REFACTOR AND MOVE TO oeTO (SUBTASK
  private void ensurePhaseTypeIsDefined(OperationsEvent oe, PortCallServiceTypeCode pp) {
    if (oe.getPortCallPhaseTypeCode() != null) {
      return;
    }
    if (oe.getPortCallServiceTypeCode() != null) {

      Set < PortCallPhaseTypeCode > validPhases =
        pp.getValidPhases().stream()
          .map(enumMappers::portCallPhaseTypeCodeCodetoDao)
          .collect(Collectors.toSet());
      if (validPhases.size() == 1) {
        PortCallPhaseTypeCode portCallPhaseTypeCode = validPhases.iterator().next();
        oe.setPortCallPhaseTypeCode(portCallPhaseTypeCode);
      }
    } else if (oe.getFacilityTypeCode() != null) {
      switch (oe.getFacilityTypeCode()) {
        case BRTH -> {
          if (oe.getOperationsEventTypeCode() == OperationsEventTypeCode.ARRI) {
            if (oe.getClassifierCode() == EventClassifierCode.ACT) {
              oe.setPortCallPhaseTypeCode(PortCallPhaseTypeCode.ALGS);
            } else {
              oe.setPortCallPhaseTypeCode(PortCallPhaseTypeCode.INBD);
            }
          }
          if (oe.getOperationsEventTypeCode() == OperationsEventTypeCode.DEPA) {
            if (oe.getClassifierCode() == EventClassifierCode.ACT) {
              oe.setPortCallPhaseTypeCode(PortCallPhaseTypeCode.OUTB);
            } else {
              oe.setPortCallPhaseTypeCode(PortCallPhaseTypeCode.ALGS);
            }
          }
                }
        case PBPL -> oe.setPortCallPhaseTypeCode(PortCallPhaseTypeCode.INBD);
      }
    }
    if (oe.getPortCallPhaseTypeCode() == null) {
      throw new IllegalStateException("Ambiguous timestamp");
    }
  }

  private void ensureValidUnLocationCode(String unLocationCode) {
    if (unLocationCode != null && unLocationRepository.findById(unLocationCode).isEmpty()) {
      throw ConcreteRequestErrorMessageException.invalidParameter(
        "UNLocation with UNLocationCode " +
          unLocationCode +
          " not part of reference implementation data set");
    }
  }
}
