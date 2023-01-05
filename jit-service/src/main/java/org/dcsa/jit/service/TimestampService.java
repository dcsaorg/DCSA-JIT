package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.mapping.PartyMapper;
import org.dcsa.jit.persistence.entity.*;
import org.dcsa.jit.persistence.entity.enums.LocationRequirement;
import org.dcsa.jit.persistence.repository.*;
import org.dcsa.jit.transferobjects.IdentifyingCodeTO;
import org.dcsa.jit.transferobjects.PartyTO;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.dcsa.jit.transferobjects.enums.DCSAResponsibleAgencyCode;
import org.dcsa.jit.transferobjects.enums.ModeOfTransport;
import org.dcsa.skernel.domain.persistence.entity.Carrier;
import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.skernel.domain.persistence.repository.AddressRepository;
import org.dcsa.skernel.domain.persistence.repository.CarrierRepository;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.dcsa.skernel.infrastructure.services.LocationService;
import org.dcsa.skernel.infrastructure.transferobject.AddressTO;
import org.dcsa.skernel.infrastructure.transferobject.LocationTO;
import org.dcsa.skernel.infrastructure.transferobject.enums.FacilityCodeListProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.dcsa.jit.persistence.entity.enums.LocationRequirement.EXCLUDED;
import static org.dcsa.jit.persistence.entity.enums.LocationRequirement.REQUIRED;

@Slf4j
@RequiredArgsConstructor
@Service
public class TimestampService {


  private static final Predicate<LocationTO> IS_FACILITY_LOCATION = LocationTO.LocationType.FACILITY.getIsType();


  private final EnumMappers enumMappers;
  private final PartyMapper partyMapper;
  private final TransportCallService transportCallService;
  private final OperationsEventRepository operationsEventRepository;
  private final TimestampDefinitionService timestampDefinitionService;
  private final SMDGDelayReasonRepository smdgDelayReasonRepository;
  private final PartyRepository partyRepository;
  private final AddressRepository addressRepository;
  private final CarrierRepository carrierRepository;
  private final JITFacilityRepository facilityRepository;
  private final TimestampRoutingService timestampRoutingService;
  private final PendingEmailNotificationRepository pendingEmailNotificationRepository;
  private final LocationService locationService;
  private final TimestampInfoRepository timestampInfoRepository;

  @Transactional
  public void createAndRouteMessage(TimestampTO timestamp) {
    if (!timestampIDExists(timestamp.timestampID())) {
      OperationsEvent operationsEvent = create(timestamp);
      timestampRoutingService.routeMessage(timestamp);
      enqueueEmailNotificationForEvent(operationsEvent);
    }
  }

  private static boolean matchAgencyCode(IdentifyingCodeTO code, DCSAResponsibleAgencyCode agencyCode) {
    return code.DCSAResponsibleAgencyCode() == agencyCode
      || agencyCode.getLegacyAgencyCode().equals(code.codeListResponsibleAgencyCode());
  }

  private void checkIdentifyingPartyCodes(TimestampTO timestamp) {
    List<IdentifyingCodeTO> partyCodes = timestamp.publisher().identifyingCodes();
    if (partyCodes == null || partyCodes.isEmpty()) {
      return;
    }
    Set<String> uneceCodes = partyCodes.stream()
      .filter(c -> matchAgencyCode(c, DCSAResponsibleAgencyCode.UNECE))
      .map(IdentifyingCodeTO::partyCode)
      .collect(Collectors.toUnmodifiableSet());
    List<IdentifyingCodeTO> smdgCodes = partyCodes.stream()
      .filter(c -> matchAgencyCode(c, DCSAResponsibleAgencyCode.SMDG))
      .toList();
    for (IdentifyingCodeTO smdgPartyCode : smdgCodes) {
      if ("TCL".equals(smdgPartyCode.codeListName())) {
        if (uneceCodes.isEmpty()) {
          throw ConcreteRequestErrorMessageException.invalidInput("SMDG TCL party codes must be accompanied by"
            + " an UN/ECE party code defining the UN Location Code (as SMDG TCL codes are only defined with"
            + " an UN Location Code).");
        }
        if (facilityRepository.findAllByFacilitySMDGCode(smdgPartyCode.partyCode()).stream()
          .map(Facility::getUNLocationCode)
          .noneMatch(uneceCodes::contains)) {
          throw ConcreteRequestErrorMessageException.invalidInput("Could not find a valid UN/ECE UN location code"
            + " matching the SMDG TCL party code \"" + smdgPartyCode.partyCode() + "\".  Please verify that"
            + " a matching SMDG TCL party code and the UN/ECE UN Location Code has been provided in the"
            + " identifyingCodes list.  Note the codes may be valid but not loaded into this system.");
        }

      } else if ("LCL".equals(smdgPartyCode.codeListName())) {
        Carrier c = carrierRepository.findBySmdgCode(smdgPartyCode.partyCode());
        if (c == null) {
          throw ConcreteRequestErrorMessageException.invalidInput("Unrecognized SMDG LCL party code \""
            + smdgPartyCode.partyCode() + "\". Note the code may be valid but not loaded into this system.");
        }
      } else {
        throw ConcreteRequestErrorMessageException.invalidInput("SMDG (306) partyCode \"" + smdgPartyCode.partyCode()
          + "\" is not classified with a (known) code list. Please use either \"TCL\" (Terminal codes) or \"LCL\" (Liner codes)"
          + " for the identifyingPartyCode. (Note the strictness of this check is an implementation detail and not"
          + " mandated by the JIT standard)");
      }
    }
  }

  @Transactional
  public OperationsEvent create(TimestampTO clientProvidedTimestamp) {
    var timestamp = normalizeTimestamp(clientProvidedTimestamp);
    TimestampDefinition timestampDefinition = timestampDefinitionService.findTimestampDefinition(timestamp);
    validateTimestamp(timestamp, timestampDefinition);

    this.ensureValidDelayReasonCode(timestamp.delayReasonCode());

    // Manually handle some entities because JPA cannot do it for us (might be easier if
    // everything used UUID as PK or other IDs that JPA knows how to handle out of the box)
    Location location = locationService.ensureResolvable(timestamp.eventLocation());
    Location vesselPos = locationService.ensureResolvable(timestamp.vesselPosition());
    Party party = savePublisher(timestamp.publisher());

    TransportCall tc = transportCallService.ensureTransportCallExists(timestamp,location);

    OperationsEvent operationsEvent =
        OperationsEvent.builder()
            .eventID(timestamp.timestampID() != null ? timestamp.timestampID() : UUID.randomUUID())
            .eventClassifierCode(
                enumMappers.eventClassifierCodetoDao(timestamp.eventClassifierCode()))
            .eventDateTime(timestamp.eventDateTime())
            .operationsEventTypeCode(
                enumMappers.operationsEventTypeCodeToDao(timestamp.operationsEventTypeCode()))
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
            .vesselDraft(timestamp.vessel() != null ? timestamp.vessel().draft() : null)
            .vesselDraftUnit(timestamp.vessel() != null ? enumMappers.dimensionUnitToDao(timestamp.vessel().dimensionUnit()) : null)
            .milesToDestinationPort(timestamp.milesToDestinationPort())
            .delayReasonCode(timestamp.delayReasonCode())
            .newRecord(true)
            .build();

    TimestampInfo ops =
      TimestampInfo.builder()
        .eventID(operationsEvent.getEventID())
        .operationsEvent(operationsEvent)
        .timestampDefinition(timestampDefinition)
        .replyToTimestampID(timestamp.replyToTimestampID())
        .newRecord(true)
        .build();

    var savedOps = timestampInfoRepository.save(ops);
    return savedOps.getOperationsEvent();
  }

  private void enqueueEmailNotificationForEvent(OperationsEvent operationsEvent) {
    pendingEmailNotificationRepository.save(PendingEmailNotification.builder()
      .eventID(operationsEvent.getEventID())
      .templateName("timestampReceived")
      .enqueuedAt(OffsetDateTime.now())
      .build());
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
                  .address(saveIfNotNull(party.getAddress(), addressRepository::save))
                  .build());
        });
  }

  private TimestampTO normalizeTimestamp(TimestampTO timestamp) {
    TimestampTO.TimestampTOBuilder timestampTOBuilder = timestamp.toBuilder();
    LocationTO locationTO = timestamp.eventLocation();
    String facilitySMDGCode = timestamp.facilitySMDGCode();
    checkIdentifyingPartyCodes(timestamp);
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
      String locationUNLocation = locationTO.UNLocationCode();
      String locationFacilityCode = locationTO.facilityCode();
      FacilityCodeListProvider locationFacilityCodeListProvider = locationTO.facilityCodeListProvider();
      if (locationUNLocation != null
          && !locationUNLocation.equals(timestamp.UNLocationCode())) {
        throw ConcreteRequestErrorMessageException.invalidInput(
            "Conflicting UNLocationCode between the timestamp and the event location");
      }

      if (locationFacilityCode == null ^ locationFacilityCodeListProvider == null) {
        if (locationFacilityCode == null) {
          throw ConcreteRequestErrorMessageException.invalidInput(
              "Cannot create location where facility code list provider is present but facility code is missing");
        }
        throw ConcreteRequestErrorMessageException.invalidInput(
            "Cannot create location where facility code is present but facility code list provider is missing");
      }

      if (locationFacilityCodeListProvider == FacilityCodeListProvider.BIC) {
        // It is complex enough without having to deal with BIC codes, so exclude BIC as an
        // implementation detail.
        throw ConcreteRequestErrorMessageException.invalidInput(
            "The reference implementation only supports SMDG codes for facilities.");
      }
      // Ensure timestamp.facilitySMDGCode is set if location.facilityCode is also set
      // The errors for misaligned values are handled below.
      if (timestamp.facilitySMDGCode() == null
          && locationFacilityCodeListProvider == FacilityCodeListProvider.SMDG) {
        timestampTOBuilder.facilitySMDGCode(locationFacilityCode);
        facilitySMDGCode = locationFacilityCode;
      }
    }
    if (facilitySMDGCode != null) {
      String locationName = locationTO != null ? locationTO.locationName() : null;
      String locationFacilityCode = locationTO != null ? locationTO.facilityCode() : null;
      FacilityCodeListProvider locationFacilityCodeListProvider = locationTO != null ?  locationTO.facilityCodeListProvider() : null;
      if (locationFacilityCodeListProvider != null
          && locationFacilityCodeListProvider != FacilityCodeListProvider.SMDG) {
        throw ConcreteRequestErrorMessageException.invalidInput(
            "Conflicting facilityCodeListProvider definition (got a facilitySMDGCode but location had a facility with provider: "
                + locationFacilityCodeListProvider
                + ")");
      }
      if (locationFacilityCode != null
          && !locationFacilityCode.equals(facilitySMDGCode)) {
        throw ConcreteRequestErrorMessageException.invalidInput(
            "Conflicting facilityCode definition (got a facilitySMDGCode but location had a facility code with a different value provider)");
      }
      locationTO = LocationTO.builder()
        .locationName(locationName)
        // We need the UNLocationCode to resolve the facility.
        .UNLocationCode(timestamp.UNLocationCode())
        .facilityCode(Objects.requireNonNullElse(timestamp.facilitySMDGCode(), locationFacilityCode))
        .facilityCodeListProvider(FacilityCodeListProvider.SMDG)
        .build();
    } else if (locationTO == null) {
      // Implementation detail: We *always* ensure that the TC has a location (so we can always rely on the
      // location.UNLocationCode)
      locationTO = LocationTO.builder()
        .locationName(null)
        .UNLocationCode(timestamp.UNLocationCode())
        .build();
    }
    if (timestamp.vessel() != null && timestamp.vessel().vesselIMONumber() != null
      && !timestamp.vesselIMONumber().equals(timestamp.vessel().vesselIMONumber())) {
      throw ConcreteRequestErrorMessageException.invalidInput(
          "Conflicting vesselIMONumber (vesselIMONumber and vessel.vesselIMONumber must be the same)");
    }

    Objects.requireNonNull(locationTO, "Internal Error: Later code assumes locationTO is always not null, but it was null");

    if (timestamp.eventLocation() != null) {
      // Include Geo & Address locations found on timestamp
      locationTO =
          locationTO.toBuilder()
              .address(timestamp.eventLocation().address())
              .latitude(timestamp.eventLocation().latitude())
              .longitude(timestamp.eventLocation().longitude())
              .build();
    }

    timestampTOBuilder.eventLocation(locationTO);

    OffsetDateTime eventCreatedDateTime = timestamp.eventCreatedDateTime() != null
      ? timestamp.eventCreatedDateTime() : OffsetDateTime.now();

    timestampTOBuilder.eventCreatedDateTime(eventCreatedDateTime);

    return timestampTOBuilder.build();
  }


  private static <T, D> D saveIfNotNull(T entity, Function<T, D> saver) {
    if (entity != null) {
      return saver.apply(entity);
    }
    return null;
  }

  private Boolean timestampIDExists(UUID timestampID) {
    return timestampID != null && operationsEventRepository.existsById(timestampID);
  }

  private void validateTimestamp(TimestampTO timestamp, TimestampDefinition timestampDefinition) {
    validateTimestampFacility(timestamp, timestampDefinition);
    validateTimestampMilesToDest(timestamp, timestampDefinition);
    validateVesselDraft(timestamp, timestampDefinition);
    validateVesselPosition(timestamp, timestampDefinition);
    validateEventLocationRequirement(timestamp, timestampDefinition);
  }

  private void validateVesselDraft(TimestampTO timestamp, TimestampDefinition timestampDefinition) {
    var draft = timestamp.vessel() != null ? timestamp.vessel().draft() : null;
    if (!timestampDefinition.getIsVesselDraftRelevant() && draft != null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Input classified as " + timestampDefinition.getTimestampTypeName()
        + ", which should not have vesselDraft specified but the input did have that field.");
    }
  }

  private void validateTimestampFacility(TimestampTO timestampTO, TimestampDefinition timestampDefinition) {
    if (timestampDefinition.getIsTerminalNeeded()
        ^ (timestampTO.facilitySMDGCode() != null
            | timestampTO.eventLocation().facilityCode() != null)) {
      if (timestampDefinition.getIsTerminalNeeded()) {
        throw ConcreteRequestErrorMessageException.invalidInput("Input classified as "
          + timestampDefinition.getTimestampTypeName()
          + ", which requires a facility but none was given (facilitySMDGCode or"
          + " eventLocation.facilityCode + eventLocation.facilityCodeListProvider)");
      }
      throw ConcreteRequestErrorMessageException.invalidInput("Input classified as "
        + timestampDefinition.getTimestampTypeName()
        + ", which should not have a facility but one was given (facilitySMDGCode and"
        + " eventLocation.facilityCode + eventLocation.facilityCodeListProvider must be null)");

    }
  }

  private void validateTimestampMilesToDest(TimestampTO timestamp, TimestampDefinition timestampDefinition) {
    if (!timestampDefinition.getIsMilesToDestinationRelevant() && timestamp.milesToDestinationPort() != null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Input classified as " + timestampDefinition.getTimestampTypeName()
        + ", which should not have milesToDestinationPort specified but the input did have that field.");

    }
  }

  private void validateVesselPosition(TimestampTO timestamp, TimestampDefinition timestampDefinition) {
    if (timestampDefinition.getVesselPositionRequirement() == EXCLUDED && timestamp.vesselPosition() != null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Input classified as " + timestampDefinition.getTimestampTypeName()
        + ", which should not have vesselPosition specified but the input did have that field.");
    }
    assert timestampDefinition.getVesselPositionRequirement() != LocationRequirement.REQUIRED;
  }

  private void validateEventLocationRequirement(TimestampTO timestamp, TimestampDefinition timestampDefinition) {
    String locationName = timestamp.eventLocation().locationName();
    boolean hasLocationName = locationName != null && !locationName.trim().isEmpty();
    LocationRequirement locationRequirement = timestampDefinition.getEventLocationRequirement();

    if (locationRequirement == REQUIRED && !hasLocationName) {
      throw ConcreteRequestErrorMessageException.invalidInput("Input classified as " + timestampDefinition.getTimestampTypeName()
        + ", which should have locationName set (not null and not empty)");
    } else if (locationRequirement == EXCLUDED && locationName != null) {
      // Using locationName != null because it provides immediate correct advise.  Otherwise, people might set it to
      // "" only to have it be caught by the exception below.
      throw ConcreteRequestErrorMessageException.invalidInput("Input classified as " + timestampDefinition.getTimestampTypeName()
        + ", which should *not* locationName set (i.e., it should be null");
    }
    if (locationName != null && !hasLocationName) {
      // Given "" or "   " is never a good locationName, we tell people to provide null or non-empty location names.
      // For most parts, this should only apply to OPTIONAL - but it feels prudent to have as a fall-safe.
      throw ConcreteRequestErrorMessageException.invalidInput("locationName should either be null or a non-empty string");
    }
  }

  private void ensureValidDelayReasonCode(String delayReasonCode){
    assertNullOrKnown(delayReasonCode, smdgDelayReasonRepository, code ->
      ConcreteRequestErrorMessageException.invalidParameter(
      "The delayReasonCode \"" + code + "\" is not included in the reference implementation data set")
    );
  }

  private static <T> void assertNullOrKnown(T key, JpaRepository<?, T> repository, Function<T, RuntimeException> exceptionFunction) {
    if (key != null && repository.findById(key).isEmpty()) {
      throw exceptionFunction.apply(key);
    }
  }
}
