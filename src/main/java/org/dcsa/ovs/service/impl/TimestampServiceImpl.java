package org.dcsa.ovs.service.impl;

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
import org.dcsa.core.events.service.LocationService;
import org.dcsa.core.events.service.OperationsEventService;
import org.dcsa.core.events.service.PartyService;
import org.dcsa.core.events.service.TransportCallTOService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.BaseServiceImpl;
import org.dcsa.core.util.MappingUtils;
import org.dcsa.ovs.model.Timestamp;
import org.dcsa.ovs.service.TimestampService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TimestampServiceImpl extends BaseServiceImpl<Timestamp, UUID> implements TimestampService {

    private final OperationsEventService operationsEventService;
    private final TransportCallRepository transportCallRepository;
    private final LocationService locationService;
    private final PartyService partyService;
    private final TransportCallTOService transportCallTOService;

    @Override
    public Flux<Timestamp> findAll() {
        return Flux.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public Mono<Timestamp> findById(UUID id) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    @Transactional
    public Mono<Timestamp> create(Timestamp timestamp) {
        if (timestamp.getModeOfTransport() == null) {
            // OVS 2.0.2 IFS says that Mode Of Transport is optional, but vessel IMO number is required.
            // The vessel IMO number is not null due to validation on the Timestamp entity.
            assert timestamp.getVesselIMONumber() != null;
            // Assume vessel IMO number implies VESSEL as mode of transport as this is only logical
            // value given IMO number is present.
            timestamp.setModeOfTransport(DCSATransportType.VESSEL);
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
        operationsEvent.setPortCallServiceTypeCode(timestamp.getPortCallServiceTypeCode());
        operationsEvent.setPublisherRole(timestamp.getPublisherRole());
        operationsEvent.setFacilityTypeCode(timestamp.getFacilityTypeCode());
        operationsEvent.setRemark(timestamp.getRemark());
        operationsEvent.setDelayReasonCode(timestamp.getDelayReasonCode());
        operationsEvent.setEventLocation(timestamp.getEventLocation());
        operationsEvent.setVesselPosition(timestamp.getVesselPositionAsLocationTO());

        if (OperationsEvent.UNKNOWN_TIMESTAMP.equals(operationsEvent.getTimestampTypeName())) {
            return Mono.error(new CreateException("Cannot derive a known timestamp name from the provided timestamp."
                    + " Please verify the contents - such as operationsEventType, eventClassifierCode,"
                    + " portCallServiceTypeCode, and portCallPhaseTypeCode."));
        }

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
                .thenReturn(timestamp);
    }

    @Override
    public Mono<Timestamp> update(Timestamp transport) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public Mono<Void> delete(Timestamp transport) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public UUID getIdOfEntity(Timestamp entity) {
        return null;
    }

    @Override
    public Flux<Timestamp> findAllExtended(ExtendedRequest<Timestamp> extendedRequest) {
        return Flux.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    private Mono<TransportCallTO> createTransportCallTO(Timestamp timestamp) {
        TransportCallTO transportCallTO = new TransportCallTO();
        Integer sequenceNumber = timestamp.getTransportCallSequenceNumber();
        transportCallTO.setTransportCallSequenceNumber(sequenceNumber != null ? sequenceNumber : 1);
        transportCallTO.setCarrierVoyageNumber(timestamp.getCarrierVoyageNumber());
        transportCallTO.setCarrierServiceCode(timestamp.getCarrierServiceCode());
        transportCallTO.setModeOfTransport(timestamp.getModeOfTransport());
        transportCallTO.setLocation(timestamp.getEventLocation());
        transportCallTO.setFacilityTypeCode(FacilityTypeCode.POTE);

        // TransportCallTOServiceImpl will create the vessel if it does not exists
        Vessel vessel = new Vessel();
        vessel.setVesselIMONumber(timestamp.getVesselIMONumber());

        List<PartyTO.IdentifyingCode> identifyingCodes = timestamp.getPublisher().getIdentifyingCodes();
        String partyCode = null;
        CarrierCodeListProvider carrierCodeListProvider = null;
        if (identifyingCodes != null) {
            for (PartyTO.IdentifyingCode code : identifyingCodes) {
                CodeListResponsibleAgency.isValidCode(code.getCodeListResponsibleAgencyCode());

                if (code.getCodeListResponsibleAgencyCode().equals(CodeListResponsibleAgency.SMDG.getCode())) {
                    partyCode = code.getPartyCode();
                    carrierCodeListProvider = CarrierCodeListProvider.SMDG;
                    break;
                } else if (code.getCodeListResponsibleAgencyCode().equals(CodeListResponsibleAgency.SCAC.getCode())) {
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

        transportCallTO.setVesselIMONumber(timestamp.getVesselIMONumber());

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
        if (timestamp.getCarrierVoyageNumber() == null ^ timestamp.getCarrierServiceCode() == null) {
            if (timestamp.getCarrierServiceCode() == null) {
                return Mono.error(new CreateException("Cannot create timestamp where voyage code is present but service code is missing"));
            }
            return Mono.error(new CreateException("Cannot create timestamp where service code is present but voyage code is missing"));
        }
        return transportCallRepository.getTransportCall(
                timestamp.getUNLocationCode(),
                modeOfTransport,
                timestamp.getVesselIMONumber(),
                timestamp.getCarrierServiceCode(),
                timestamp.getCarrierVoyageNumber(),
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
