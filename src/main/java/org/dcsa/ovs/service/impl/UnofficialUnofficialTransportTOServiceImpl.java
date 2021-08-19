package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.TransportCall;
import org.dcsa.core.events.model.base.AbstractTransportCall;
import org.dcsa.ovs.model.transferobjects.ShallowTransportCallTO;
import org.dcsa.core.events.service.TransportCallService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.util.MappingUtils;
import org.dcsa.core.events.model.Transport;
import org.dcsa.ovs.model.transferobjects.UnofficialTransportTO;
import org.dcsa.ovs.repository.FacilityRepository;
import org.dcsa.ovs.repository.TransportRepository;
import org.dcsa.ovs.repository.UnofficialTransportTORepository;
import org.dcsa.ovs.service.UnofficialTransportTOService;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Service
public class UnofficialUnofficialTransportTOServiceImpl extends ExtendedBaseServiceImpl<UnofficialTransportTORepository, UnofficialTransportTO, UUID> implements UnofficialTransportTOService {

    private final FacilityRepository facilityRepository;
    private final TransportCallService transportCallService;
    private final TransportRepository transportRepository;
    private final UnofficialTransportTORepository unofficialTransportTORepository;
    private final ExtendedParameters extendedParameters;
    private final R2dbcDialect r2dbcDialect;

    @Override
    public Flux<UnofficialTransportTO> findAll() {
        ExtendedRequest<UnofficialTransportTO> extendedRequest = newExtendedRequest();
        return unofficialTransportTORepository.findAllExtended(extendedRequest);
    }

    @Override
    public Mono<UnofficialTransportTO> findById(UUID id) {
        ExtendedRequest<UnofficialTransportTO> extendedRequest = newExtendedRequest();
        extendedRequest.parseParameter(Map.of("transportID", List.of(String.valueOf(id))));
        return unofficialTransportTORepository.findAllExtended(extendedRequest)
                .take(2)
                .collectList()
                .flatMap(transportTOs -> {
                    if (transportTOs.size() > 1) {
                        throw new AssertionError("transportID is not unique");
                    }
                    if (transportTOs.isEmpty()) {
                        return Mono.empty();
                    }
                    return Mono.just(transportTOs.get(0));
                });
    }

    @Override
    public Mono<UnofficialTransportTO> create(UnofficialTransportTO transport) {
        return Flux.concat(
                mapTransportCall(transport.getDischargeTransportCall())
                        .flatMap(transportCallService::create)
                        .doOnNext(tc -> transport.setDischargeTransportCallID(tc.getTransportCallID())),
                mapTransportCall(transport.getLoadTransportCall())
                        .flatMap(transportCallService::create)
                        .doOnNext(tc -> transport.setLoadTransportCallID(tc.getTransportCallID()))
        ).then(transportRepository.save(transport))
                .map(Transport::getTransportID)
                .flatMap(this::findById);
    }

    private Mono<TransportCall> mapTransportCall(ShallowTransportCallTO transportCallTO) {
        TransportCall transportCall = MappingUtils.instanceFrom(transportCallTO, TransportCall::new, AbstractTransportCall.class);
        return mapEntity(transportCallTO)
                .doOnNext(facility -> transportCall.setFacilityID(facility.getFacilityID()))
                .thenReturn(transportCall);
    }

    private Mono<Facility> mapEntity(ShallowTransportCallTO transportCallTO) {
        BiFunction<String, String, Mono<Facility>> method;
        if (transportCallTO.getFacilityCodeListProvider() == null) {
            throw new CreateException("facility code list provider is required");
        }
        switch (transportCallTO.getFacilityCodeListProvider()) {
            case SMDG:
                method = facilityRepository::findByUnLocationCodeAndFacilitySMGDCode;
                break;
            case BIC:
                method = facilityRepository::findByUnLocationCodeAndFacilityBICCode;
                break;
            default:
                throw new CreateException("Unsupported facility code list provider: " + transportCallTO.getFacilityCodeListProvider());
        }
        return method.apply(transportCallTO.getUNLocationCode(), transportCallTO.getFacilityCode())
                .switchIfEmpty(Mono.error(new CreateException("Cannot find any facility with code "
                        + transportCallTO.getFacilityCode() + " (UN Locode: " + transportCallTO.getUNLocationCode()
                        + ", provider: " + transportCallTO.getFacilityCodeListProvider() +  ")")));
    }

    @Override
    public Mono<UnofficialTransportTO> update(UnofficialTransportTO transport) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public Mono<Void> delete(UnofficialTransportTO transport) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public UnofficialTransportTORepository getRepository() {
        return unofficialTransportTORepository;
    }

    @Override
    public UUID getIdOfEntity(UnofficialTransportTO entity) {
        return entity.getTransportID();
    }


    public ExtendedRequest<UnofficialTransportTO> newExtendedRequest() {
        return new ExtendedRequest<>(extendedParameters, r2dbcDialect, UnofficialTransportTO.class);
    }

}
