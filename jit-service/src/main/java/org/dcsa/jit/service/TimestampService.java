package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.mapping.LocationMapper;
import org.dcsa.jit.mapping.PartyMapper;
import org.dcsa.jit.persistence.entity.*;
import org.dcsa.jit.persistence.entity.enums.LocationRequirement;
import org.dcsa.jit.persistence.repository.*;
import org.dcsa.jit.transferobjects.LocationTO;
import org.dcsa.jit.transferobjects.PartyTO;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.dcsa.jit.transferobjects.enums.FacilityCodeListProvider;
import org.dcsa.jit.transferobjects.enums.ModeOfTransport;
import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.function.Function;

import static org.dcsa.jit.persistence.entity.enums.LocationRequirement.EXCLUDED;
import static org.dcsa.jit.persistence.entity.enums.LocationRequirement.REQUIRED;

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
  private final SMDGDelayReasonRepository smdgDelayReasonRepository;
  private final UnLocationRepository unLocationRepository;
  private final LocationRepository locationRepository;
  private final UnmappedEventRepository unmappedEventRepository;
  private final PartyRepository partyRepository;
  private final AddressRepository addressRepository;
  private final FacilityRepository facilityRepository;
  private final TimestampRoutingService timestampRoutingService;
  private final PendingEmailNotificationRepository pendingEmailNotificationRepository;

  @Transactional
  public void createAndRouteMessage(TimestampTO timestamp) {
    OperationsEvent operationsEvent = create(timestamp);
    timestampRoutingService.routeMessage(timestamp);
    enqueueEmailNotificationForEvent(operationsEvent);
  }

  @Transactional
  public OperationsEvent create(TimestampTO timestamp) {
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
            "The reference implementation only supports SMDG codes for facilities.");
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
    } else if (locationTO == null) {
      // Implementation detail: We *always* ensure that the TC has a location (so we can always rely on the
      // location.UNLocationCode)
      locationTO = LocationTO.builder()
        .UNLocationCode(timestamp.UNLocationCode())
        .build();
    }
    if (timestamp.vessel() != null && timestamp.vessel().vesselIMONumber() != null
      && !timestamp.vesselIMONumber().equals(timestamp.vessel().vesselIMONumber())) {
      throw ConcreteRequestErrorMessageException.invalidInput(
          "Conflicting vesselIMONumber (vesselIMONumber and vessel.vesselIMONumber must be the same)");
    }

    timestampTOBuilder.eventLocation(locationTO);
    timestamp = timestampTOBuilder.build();

    this.ensureValidUnLocationCode(
        (timestamp.eventLocation()) == null ? null : timestamp.eventLocation().UNLocationCode());
    this.ensureValidUnLocationCode(timestamp.UNLocationCode());
    this.ensureValidDelayReasonCode(timestamp.delayReasonCode());

    // Manually handle some entities because JPA cannot do it for us (might be easier if
    // everything used UUID as PK or other IDs that JPA knows how to handle out of the box)
    Location location = saveLocationIfNotNull(timestamp.eventLocation());
    Location vesselPos = saveLocationIfNotNull(timestamp.vesselPosition());
    Party party = savePublisher(timestamp.publisher());

    TransportCall tc = transportCallService.ensureTransportCallExists(timestamp,location);

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
            .vesselDraft(timestamp.vessel() != null ? timestamp.vessel().draft() : null)
            .vesselDraftUnit(timestamp.vessel() != null ? enumMappers.dimensionUnitToDao(timestamp.vessel().dimensionUnit()) : null)
            .milesRemainingToDestination(timestamp.milesToDestinationPort())
            .delayReasonCode(timestamp.delayReasonCode())
            .build();

    return create(operationsEvent);
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

  private Location saveLocationIfNotNull(LocationTO locationTO) {
    return saveIfNotNull(
        locationTO,
        lTO -> {
          Location l = locationMapper.toDao(lTO);
          return locationRepository.save(
              l.toBuilder()
                  .facility(ensureValidFacilityCode(locationTO))
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

  public OperationsEvent create(OperationsEvent operationsEvent) {

    operationsEvent = operationsEventRepository.save(operationsEvent);
    TimestampDefinition timestampDefinition = timestampDefinitionService.markOperationsEventAsTimestamp(operationsEvent);

    validateTimestamp(operationsEvent, timestampDefinition);

    UnmappedEvent unmappedEvent =
        UnmappedEvent.builder()
            .eventID(operationsEvent.getEventID())
            .enqueuedAtDateTime(operationsEvent.getEventDateTime())
            .newRecord(true)
            .build();
    unmappedEventRepository.save(unmappedEvent);
    return operationsEvent;
  }

  private void validateTimestamp(OperationsEvent operationsEvent, TimestampDefinition timestampDefinition) {
    validateTimestampFacility(operationsEvent, timestampDefinition);
    validateTimestampMilesToDest(operationsEvent, timestampDefinition);
    validateVesselPosition(operationsEvent, timestampDefinition);
    validateEventLocationRequirement(operationsEvent, timestampDefinition);
  }

  private void validateTimestampFacility(OperationsEvent operationsEvent, TimestampDefinition timestampDefinition) {
    if (timestampDefinition.getIsTerminalNeeded() ^ operationsEvent.getEventLocation().getFacility() != null) {
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

  private void validateTimestampMilesToDest(OperationsEvent operationsEvent, TimestampDefinition timestampDefinition) {
    if (!timestampDefinition.getIsMilesToDestinationRelevant() && operationsEvent.getMilesRemainingToDestination() != null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Input classified as " + timestampDefinition.getTimestampTypeName()
        + ", which should not have milesToDestinationPort specified but the input did have that field.");

    }
  }

  private void validateVesselPosition(OperationsEvent operationsEvent, TimestampDefinition timestampDefinition) {
    if (timestampDefinition.getVesselPositionRequirement() == EXCLUDED && operationsEvent.getVesselPosition() != null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Input classified as " + timestampDefinition.getTimestampTypeName()
        + ", which should not have vesselPosition specified but the input did have that field.");
    }
    assert timestampDefinition.getVesselPositionRequirement() != LocationRequirement.REQUIRED;
  }

  private void validateEventLocationRequirement(OperationsEvent operationsEvent, TimestampDefinition timestampDefinition) {
    String locationName =  operationsEvent.getEventLocation().getLocationName();
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

  private void ensureValidUnLocationCode(String unLocationCode) {
    assertNullOrKnown(unLocationCode, unLocationRepository, code ->
      ConcreteRequestErrorMessageException.invalidParameter(
        "UNLocation with UNLocationCode "
          + unLocationCode
          + " not part of reference implementation data set")
    );
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

  private Facility ensureValidFacilityCode(LocationTO locationTO) {
    if (locationTO.UNLocationCode() != null
        && locationTO.facilityCode() != null
        && locationTO.facilityCodeListProvider() != null) {
      Optional<Facility> facilityCode = Optional.empty();
      if (locationTO.facilityCodeListProvider() == FacilityCodeListProvider.SMDG) {
        facilityCode =
            facilityRepository.findByUNLocationCodeAndFacilitySMDGCode(
                locationTO.UNLocationCode(), locationTO.facilityCode());
      } else if (locationTO.facilityCodeListProvider() == FacilityCodeListProvider.BIC) {
        facilityCode =
            facilityRepository.findByUNLocationCodeAndFacilityBICCode(
                locationTO.UNLocationCode(), locationTO.facilityCode());
      }
      return facilityCode.orElseThrow(
          () ->
              ConcreteRequestErrorMessageException.invalidParameter(
                  "UNLocation with UNLocationCode "
                      + locationTO.UNLocationCode()
                      + " does not have a facility with facilityCode "
                      + locationTO.facilityCode()));
    }
    return null;
  }
}
