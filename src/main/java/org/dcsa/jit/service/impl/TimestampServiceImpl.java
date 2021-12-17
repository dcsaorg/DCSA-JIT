package org.dcsa.jit.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.TransportCall;
import org.dcsa.core.events.model.Vessel;
import org.dcsa.core.events.model.base.AbstractTransportCall;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.events.repository.TransportCallRepository;
import org.dcsa.core.events.service.*;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.util.MappingUtils;
import org.dcsa.jit.model.Payload;
import org.dcsa.jit.model.Timestamp;
import org.dcsa.jit.repository.OpsEventTimestampDefinitionRepository;
import org.dcsa.jit.repository.PayloadRepository;
import org.dcsa.jit.service.TimestampService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class TimestampServiceImpl implements TimestampService {

    private final OperationsEventService operationsEventService;
    private final TransportCallRepository transportCallRepository;
    private final LocationService locationService;
    private final PartyService partyService;
    private final OpsEventTimestampDefinitionRepository opsEventTimestampDefinitionRepository;
    private final PayloadRepository payloadRepository;
    private final TransportCallTOService transportCallTOService;

    @Override
    @Transactional
    public Mono<Timestamp> create(Timestamp timestamp, byte[] originalPayload) {
        if (timestamp.getModeOfTransport() == null) {
            // JIT IFS says that Mode Of Transport must be omitted for some timestamps and must be VESSEL for others.
            // Because the distinction is not visible after the timestamp has been created, so we cannot rely on it
            // in general either way.
            timestamp.setModeOfTransport(DCSATransportType.VESSEL);
        }
        if (!timestamp.getModeOfTransport().equals(DCSATransportType.VESSEL)) {
            return Mono.error(new CreateException("modeOfTransport must be blank or \"VESSEL\""));
        }

        LocationTO location = timestamp.getEventLocation();
        if (location != null) {
            if (location.getUnLocationCode() != null && !location.getUnLocationCode().equals(timestamp.getUNLocationCode())) {
                return Mono.error(new CreateException("Conflicting UNLocationCode between the timestamp and the event location"));
            }
            if (location.getFacilityCode() == null ^ location.getFacilityCodeListProvider() == null) {
                if (location.getFacilityCode() == null) {
                    return Mono.error(new CreateException("Cannot create location where facility code list provider is present but facility code is missing"));
                }
                return Mono.error(new CreateException("Cannot create location where facility code is present but facility code list provider is missing"));
            }
        }
        if (timestamp.getFacilitySMDGCode() != null) {
            if (location == null) {
                location = new LocationTO();
                timestamp.setEventLocation(location);
            }
            // We need the UNLocode to resolve the facility.
            location.setUnLocationCode(timestamp.getUNLocationCode());
            if (location.getFacilityCodeListProvider() != null && location.getFacilityCodeListProvider() != FacilityCodeListProvider.SMDG) {
                return Mono.error(new CreateException("Conflicting facilityCodeListProvider definition (got a facilitySMDGCode but location had a facility with provider: " + location.getFacilityCodeListProvider() + ")"));
            }
            if (location.getFacilityCode() != null && !location.getFacilityCode().equals(timestamp.getFacilitySMDGCode())) {
                return Mono.error(new CreateException("Conflicting facilityCode definition (got a facilitySMDGCode but location had a facility code with a different value provider)"));
            }
            location.setFacilityCodeListProvider(FacilityCodeListProvider.SMDG);
            location.setFacilityCode(timestamp.getFacilitySMDGCode());
        }
        OperationsEvent operationsEvent = new OperationsEvent();
        operationsEvent.setEventClassifierCode(timestamp.getEventClassifierCode());
        operationsEvent.setEventDateTime(timestamp.getEventDateTime());
        operationsEvent.setOperationsEventTypeCode(timestamp.getOperationsEventTypeCode());
        operationsEvent.setPortCallPhaseTypeCode(timestamp.getPortCallPhaseTypeCode());
        operationsEvent.setPortCallServiceTypeCode(timestamp.getPortCallServiceTypeCode());
        operationsEvent.setPublisherRole(timestamp.getPublisherRole());
        operationsEvent.setFacilityTypeCode(timestamp.getFacilityTypeCode());
        operationsEvent.setRemark(timestamp.getRemark());
        operationsEvent.setDelayReasonCode(timestamp.getDelayReasonCode());
        operationsEvent.setEventLocation(timestamp.getEventLocation());
        operationsEvent.setVesselPosition(timestamp.getVesselPositionAsLocationTO());

        return this.findTransportCall(timestamp)
                .map(transportCall -> MappingUtils.instanceFrom(transportCall, TransportCallTO::new, AbstractTransportCall.class))
                // Create transport call if missing
                .switchIfEmpty(createTransportCallTO(timestamp))
                .flatMap(transportCallTO -> {
                    operationsEvent.setTransportCallID(transportCallTO.getTransportCallID());
                    operationsEvent.setTransportCall(transportCallTO);

                    return Mono.just(timestamp.getPublisher())
                            .flatMap(partyService::ensureResolvable)
                            .doOnNext(party -> operationsEvent.setPublisherID(party.getId()))
                            .thenReturn(operationsEvent);
                })
                .then(Mono.justOrEmpty(operationsEvent.getEventLocation())
                        .flatMap(locationService::ensureResolvable)
                        .doOnNext(location2 -> operationsEvent.setEventLocationID(location2.getId()))
                        .thenReturn(operationsEvent)
                ).then(Mono.justOrEmpty(operationsEvent.getVesselPosition())
                        .flatMap(locationService::ensureResolvable)
                        .doOnNext(vesselPosition2 -> operationsEvent.setVesselPositionID(vesselPosition2.getId()))
                        .thenReturn(operationsEvent)
                ).flatMap(operationsEventService::create)
                .flatMap(opsEvent -> Mono.justOrEmpty(originalPayload)
                        .map(Payload::of)
                        .flatMap(payloadRepository::save)
                        .map(Payload::getPayloadID)
                        .flatMap(payloadID -> opsEventTimestampDefinitionRepository.linkPayload(opsEvent.getEventID(), payloadID))
                ).thenReturn(timestamp);
    }


    private Mono<TransportCallTO> createTransportCallTO(Timestamp timestamp) {
        TransportCallTO transportCallTO = new TransportCallTO();
        Integer sequenceNumber = timestamp.getTransportCallSequenceNumber();
        transportCallTO.setTransportCallSequenceNumber(sequenceNumber != null ? sequenceNumber : 1);
        transportCallTO.setExportVoyageNumber(timestamp.getExportVoyageNumber());
        transportCallTO.setImportVoyageNumber(timestamp.getImportVoyageNumber());
        transportCallTO.setCarrierServiceCode(timestamp.getCarrierServiceCode());
        transportCallTO.setModeOfTransport(timestamp.getModeOfTransport());
        transportCallTO.setLocation(timestamp.getEventLocation());
        transportCallTO.setFacilityTypeCode(FacilityTypeCode.POTE);

        // TransportCallTOServiceImpl will create the vessel if it does not exist
        Vessel vessel = new Vessel();
        vessel.setVesselIMONumber(timestamp.getVesselIMONumber());

        List<PartyTO.IdentifyingCode> identifyingCodes = timestamp.getPublisher().getIdentifyingCodes();
        String partyCode = null;
        CarrierCodeListProvider carrierCodeListProvider = null;
        if (identifyingCodes != null) {
            for (PartyTO.IdentifyingCode code : identifyingCodes) {
                DCSAResponsibleAgencyCode.ensureIsValidLegacyCode(code.getCodeListResponsibleAgencyCode());

                if (code.getCodeListResponsibleAgencyCode().equals(DCSAResponsibleAgencyCode.SMDG.getLegacyAgencyCode())) {
                    partyCode = code.getPartyCode();
                    carrierCodeListProvider = CarrierCodeListProvider.SMDG;
                    break;
                } else if (code.getCodeListResponsibleAgencyCode().equals(DCSAResponsibleAgencyCode.SCAC.getLegacyAgencyCode())) {
                    partyCode = code.getPartyCode();
                    carrierCodeListProvider = CarrierCodeListProvider.NMFTA;
                }
            }
        }

        if (partyCode != null && carrierCodeListProvider != null) {
            vessel.setVesselOperatorCarrierCode(partyCode);
            vessel.setVesselOperatorCarrierCodeListProvider(carrierCodeListProvider);
        }

        transportCallTO.setVessel(vessel);

        // Note that the facility of the timestamp is *NOT* related to the transport call itself.
        // Therefore we use a location to store the UNLocationCode
        LocationTO transportCallLocation = new LocationTO();
        transportCallLocation.setUnLocationCode(timestamp.getUNLocationCode());
        transportCallTO.setLocation(transportCallLocation);

        return transportCallTOService.create(transportCallTO);
    }

    private Mono<TransportCall> findTransportCall(Timestamp timestamp) {
        // Caller should have ensured that Mode of Transport is not null at this point.
        String modeOfTransport = Objects.requireNonNull(timestamp.getModeOfTransport()).name();
        Integer sequenceNumber = timestamp.getTransportCallSequenceNumber();
        if (timestamp.getExportVoyageNumber() != null ^ timestamp.getImportVoyageNumber() != null) {
            return Mono.error(new CreateException("exportVoyageNumber and importVoyageNumber must be given together or not at all"));
        }
        if (timestamp.getExportVoyageNumber() != null) {
            if (timestamp.getCarrierVoyageNumber() != null) {
                return Mono.error(new CreateException("Obsolete carrierVoyageNumber cannot be used with exportVoyageNumber + importVoyageNumber. Please omit it!"));
            }
        } else {
            timestamp.setExportVoyageNumber(timestamp.getCarrierVoyageNumber());
            timestamp.setImportVoyageNumber(timestamp.getCarrierVoyageNumber());
        }
        if (timestamp.getCarrierServiceCode() == null) {
            return Mono.error(new CreateException("Cannot create timestamp where service code is missing"));
        }
        if (timestamp.getExportVoyageNumber() == null) {
            return Mono.error(new CreateException("Cannot create timestamp where voyage number (carrierVoyageNumber OR exportVoyageNumber + importVoyageNumber) is missing"));
        }
        return transportCallRepository.getTransportCall(
                timestamp.getUNLocationCode(),
                modeOfTransport,
                timestamp.getVesselIMONumber(),
                timestamp.getCarrierServiceCode(),
                timestamp.getImportVoyageNumber(),
                timestamp.getExportVoyageNumber(),
                sequenceNumber
        ).take(2)
                .collectList()
                .flatMap(transportCalls -> {
                    if (transportCalls.isEmpty()) {
                        return Mono.empty();
                    }
                    if (transportCalls.size() > 1) {
                        if (timestamp.getCarrierServiceCode() == null) {
                            if (sequenceNumber == null) {
                                return Mono.error(new CreateException("Ambiguous transport call; please define sequence number or/and carrier service code + voyage number"));
                            }
                            return Mono.error(new CreateException("Ambiguous transport call; please define carrier service code and voyage number"));
                        }
                        if (sequenceNumber == null) {
                            return Mono.error(new CreateException("Ambiguous transport call; please define sequence number"));
                        }
                        return Mono.error(new AssertionError("Internal error: Ambitious transport call; the result should be unique but is not"));
                    }
                    return Mono.just(transportCalls.get(0));
                });
    }
}
