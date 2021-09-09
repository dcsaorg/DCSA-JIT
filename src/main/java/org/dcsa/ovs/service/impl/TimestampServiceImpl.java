package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.IdentifyingCode;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.Vessel;
import org.dcsa.core.events.model.base.AbstractTransportCall;
import org.dcsa.core.events.model.enums.CarrierCodeListProvider;
import org.dcsa.core.events.model.enums.DCSATransportType;
import org.dcsa.core.events.model.enums.FacilityCodeListProvider;
import org.dcsa.core.events.model.transferobjects.FacilityTO;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.events.repository.TransportCallRepository;
import org.dcsa.core.events.service.LocationService;
import org.dcsa.core.events.service.OperationsEventService;
import org.dcsa.core.events.service.PartyService;
import org.dcsa.core.events.service.TransportCallTOService;
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
        if (timestamp.getFacilitySMDGCode() == null) {
            // OVS 2.0.0 Spec says optional, but our code does not function without it.  Let's be honest about it.
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED));
        }
        if (timestamp.getModeOfTransport() == null) {
            if (timestamp.getVesselIMONumber() == null) {
                // OVS 2.0.0 Spec says optional, operations event says mandatory.  The latter wins for now.
                return Mono.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED));
            }
            // Assume vessel IMO number implies VESSEL as mode of transport.
            timestamp.setModeOfTransport(DCSATransportType.VESSEL);
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

        String modeOfTransport = timestamp.getModeOfTransport() != null ? timestamp.getModeOfTransport().name() : null;

        return transportCallRepository.getTransportCall(timestamp.getUNLocationCode(), timestamp.getFacilitySMDGCode(), modeOfTransport, timestamp.getVesselIMONumber())
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
                .flatMap(ignored -> Mono.justOrEmpty(operationsEvent.getEventLocation())
                        .flatMap(locationService::ensureResolvable)
                        .doOnNext(location2 -> operationsEvent.setEventLocationID(location2.getId()))
                        .thenReturn(operationsEvent)
                ).flatMap(ignored -> Mono.justOrEmpty(operationsEvent.getVesselPosition())
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
        transportCallTO.setTransportCallSequenceNumber(timestamp.getTransportCallSequenceNumber());
        transportCallTO.setCarrierVoyageNumber(timestamp.getCarrierVoyageNumber());
        transportCallTO.setCarrierServiceCode(timestamp.getCarrierServiceCode());
        transportCallTO.setModeOfTransport(timestamp.getModeOfTransport());
        transportCallTO.setLocation(timestamp.getEventLocation());

        // TransportCallTOServiceImpl will create the vessel if it does not exists
        Vessel vessel = new Vessel();
        vessel.setVesselIMONumber(timestamp.getVesselIMONumber());

        List<IdentifyingCode> identifyingCodes = timestamp.getPublisher().getIdentifyingCodes();
        IdentifyingCode identifyingCode = null;
        for (IdentifyingCode code : identifyingCodes) {
            CarrierCodeListProvider codeListResponsibleAgencyCode = CarrierCodeListProvider.fromCodeListResponsibleAgencyCode(code.getCodeListResponsibleAgencyCode());
            if (codeListResponsibleAgencyCode == null) {
                continue;
            }

            identifyingCode = code;

            if (codeListResponsibleAgencyCode == CarrierCodeListProvider.SMDG) {
                break;
            }
        }
        if (identifyingCode != null) {
            vessel.setVesselOperatorCarrierCode(identifyingCode.getPartyCode());
            CarrierCodeListProvider codeListResponsibleAgencyCode = CarrierCodeListProvider.fromCodeListResponsibleAgencyCode(identifyingCode.getCodeListResponsibleAgencyCode());
            vessel.setVesselOperatorCarrierCodeListProvider(codeListResponsibleAgencyCode);
        }

        transportCallTO.setVessel(vessel);

        transportCallTO.setVesselIMONumber(timestamp.getVesselIMONumber());

        // Facility
        transportCallTO.setUNLocationCode(timestamp.getUNLocationCode());
        transportCallTO.setFacilityCodeListProvider(FacilityCodeListProvider.SMDG);
        transportCallTO.setFacilityCode(timestamp.getFacilitySMDGCode());

        transportCallTO.setFacilityTypeCode(timestamp.getFacilityTypeCode());

        return transportCallTOService.create(transportCallTO);
    }
}
