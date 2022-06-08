package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.mapping.LocationMapper;
import org.dcsa.jit.mapping.PartyMapper;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.persistence.entity.UnmappedEvent;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;
import org.dcsa.jit.persistence.entity.enums.OperationsEventTypeCode;
import org.dcsa.jit.persistence.entity.enums.PortCallPhaseTypeCode;
import org.dcsa.jit.persistence.repository.OperationsEventRepository;
import org.dcsa.jit.persistence.repository.UnLocationRepository;
import org.dcsa.jit.persistence.repository.UnmappedEventRepository;
import org.dcsa.jit.transferobjects.LocationTO;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.dcsa.jit.transferobjects.enums.FacilityCodeListProvider;
import org.dcsa.jit.transferobjects.enums.ModeOfTransport;
import org.dcsa.jit.transferobjects.enums.PortCallServiceTypeCode;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
  private final UnmappedEventRepository unmappedEventRepository;

  @Transactional
  public void create(TimestampTO timestamp, byte[] originalPayload) {
    if (timestamp.modeOfTransport() == null) {
      // JIT IFS says that Mode Of Transport must be omitted for some timestamps and must be VESSEL
      // for others.
      // Because the distinction is not visible after the timestamp has been created, so we cannot
      // rely on it
      // in general either way.
      timestamp.toBuilder().modeOfTransport(ModeOfTransport.VESSEL).build();
    }
    if (!timestamp.modeOfTransport().equals(ModeOfTransport.VESSEL)) {
      throw ConcreteRequestErrorMessageException.invalidInput(
          "modeOfTransport must be blank or \"VESSEL\"");
    }

    LocationTO location = timestamp.eventLocation();
    if (location != null) {
      if (location.unLocationCode() != null
          && !location.unLocationCode().equals(timestamp.unLocationCode())) {
        throw ConcreteRequestErrorMessageException.invalidInput(
            "Conflicting UNLocationCode between the timestamp and the event location");
      }
      if (location.facilityCode() == null ^ location.facilityCodeListProvider() == null) {
        if (location.facilityCode() == null) {
          throw ConcreteRequestErrorMessageException.invalidInput(
              "Cannot create location where facility code list provider is present but facility code is missing");
        }
        throw ConcreteRequestErrorMessageException.invalidInput(
            "Cannot create location where facility code is present but facility code list provider is missing");
      }
      if (location.facilityCodeListProvider() == FacilityCodeListProvider.BIC) {
        // It is complex enough without having to deal with BIC codes, so exclude BIC as an
        // implementation detail.
        throw ConcreteRequestErrorMessageException.invalidInput(
            "The reference implementation only includes supports SMDG codes for facilities.");
      }
      // Ensure timestamp.facilitySMDGCode and location.facilityCode is aligned
      // The errors for misaligned values are handled below.
      if (timestamp.facilitySMDGCode() == null
          && location.facilityCodeListProvider() == FacilityCodeListProvider.SMDG) {
        timestamp.toBuilder().facilitySMDGCode(location.facilityCode()).build();
      }
    }
    if (timestamp.facilitySMDGCode() != null) {
      if (location == null) {
        location = LocationTO.builder().build();
        timestamp.toBuilder().eventLocation(location).build();
      }
      // We need the UNLocode to resolve the facility.
      location.toBuilder().unLocationCode(timestamp.unLocationCode()).build();
      if (location.facilityCodeListProvider() != null
          && location.facilityCodeListProvider() != FacilityCodeListProvider.SMDG) {
        throw ConcreteRequestErrorMessageException.invalidInput(
            "Conflicting facilityCodeListProvider definition (got a facilitySMDGCode but location had a facility with provider: "
                + location.facilityCodeListProvider()
                + ")");
      }
      if (location.facilityCode() != null
          && !location.facilityCode().equals(timestamp.facilitySMDGCode())) {
        throw ConcreteRequestErrorMessageException.invalidInput(
            "Conflicting facilityCode definition (got a facilitySMDGCode but location had a facility code with a different value provider)");
      }
      location.toBuilder()
          .facilityCodeListProvider(FacilityCodeListProvider.SMDG)
          .facilityCode(timestamp.facilitySMDGCode())
          .build();
    }

    this.ensureValidUnLocationCode(Objects.requireNonNull(timestamp.eventLocation()).unLocationCode());
    this.ensureValidUnLocationCode(timestamp.unLocationCode());

    TransportCall tc = transportCallService.ensureTransportCallExists(timestamp);

    OperationsEvent operationsEvent =
        OperationsEvent.builder()
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
            .location(locationMapper.toDao(timestamp.eventLocation()))
            .vesselPosition(locationMapper.toDao(timestamp.vesselPosition()))
            .publisher(partyMapper.toDao(timestamp.publisher()))
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

    timestampDefinitionService.markOperationsEventAsTimestamp(operationsEvent);

    operationsEventRepository.save(operationsEvent);

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

      Set<PortCallPhaseTypeCode> validPhases =
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
          "UNLocation with UNLocationCode "
              + unLocationCode
              + " not part of reference implementation data set");
    }
  }
}
