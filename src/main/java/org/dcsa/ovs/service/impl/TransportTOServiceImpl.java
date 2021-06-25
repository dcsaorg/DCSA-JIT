package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.util.MappingUtils;
import org.dcsa.ovs.model.Facility;
import org.dcsa.ovs.model.Transport;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.model.base.AbstractTransportCall;
import org.dcsa.ovs.model.transferobjects.TransportCallTO;
import org.dcsa.ovs.model.transferobjects.TransportTO;
import org.dcsa.ovs.repository.FacilityRepository;
import org.dcsa.ovs.repository.TransportRepository;
import org.dcsa.ovs.repository.TransportTORepository;
import org.dcsa.ovs.service.TransportCallService;
import org.dcsa.ovs.service.TransportTOService;
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
public class TransportTOServiceImpl extends ExtendedBaseServiceImpl<TransportTORepository, TransportTO, UUID> implements TransportTOService {

    private final FacilityRepository facilityRepository;
    private final TransportCallService transportCallService;
    private final TransportRepository transportRepository;
    private final TransportTORepository transportTORepository;
    private final ExtendedParameters extendedParameters;
    private final R2dbcDialect r2dbcDialect;

    @Override
    public Flux<TransportTO> findAll() {
        ExtendedRequest<TransportTO> extendedRequest = newExtendedRequest();
        return transportTORepository.findAllExtended(extendedRequest);
    }

    @Override
    public Mono<TransportTO> findById(UUID id) {
        ExtendedRequest<TransportTO> extendedRequest = newExtendedRequest();
        extendedRequest.parseParameter(Map.of("transportID", List.of(String.valueOf(id))));
        return transportTORepository.findAllExtended(extendedRequest)
                .single();
    }

    @Override
    public Mono<TransportTO> create(TransportTO transport) {
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

    private Mono<TransportCall> mapTransportCall(TransportCallTO transportCallTO) {
        TransportCall transportCall = MappingUtils.instanceFrom(transportCallTO, TransportCall::new, AbstractTransportCall.class);
        return mapEntity(transportCallTO)
                .doOnNext(facility -> transportCall.setFacilityID(facility.getFacilityID()))
                .thenReturn(transportCall);
    }

    private Mono<Facility> mapEntity(TransportCallTO transportCallTO) {
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
    public Mono<TransportTO> update(TransportTO transport) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public Mono<Void> delete(TransportTO transport) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public TransportTORepository getRepository() {
        return transportTORepository;
    }

    @Override
    public UUID getIdOfEntity(TransportTO entity) {
        return entity.getTransportID();
    }


    public ExtendedRequest<TransportTO> newExtendedRequest() {
        return new ExtendedRequest<>(extendedParameters, r2dbcDialect, TransportTO.class);
    }

}
