package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.TransportCall;
import org.dcsa.core.events.model.base.AbstractTransportCall;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.events.service.LocationService;
import org.dcsa.core.events.service.TransportCallService;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.util.MappingUtils;
import org.dcsa.ovs.repository.TransportCallTORepository;
import org.dcsa.ovs.service.TransportCallTOService;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class TransportCallTOServiceImpl extends ExtendedBaseServiceImpl<TransportCallTORepository, TransportCallTO, String> implements TransportCallTOService {

    private final LocationService locationService;
    private final TransportCallTORepository transportCallTORepository;
    private final TransportCallService transportCallService;
    private final ExtendedParameters extendedParameters;
    private final R2dbcDialect r2dbcDialect;

    @Override
    public Flux<TransportCallTO> findAll() {
        ExtendedRequest<TransportCallTO> extendedRequest = newExtendedRequest();
        return transportCallTORepository.findAllExtended(extendedRequest);
    }

    @Override
    public Mono<TransportCallTO> findById(String id) {
        ExtendedRequest<TransportCallTO> extendedRequest = newExtendedRequest();
        extendedRequest.parseParameter(Map.of("transportCallID", List.of(id)));
        return transportCallTORepository.findAllExtended(extendedRequest)
                .single();
    }

    @Override
    public Mono<TransportCallTO> create(TransportCallTO transportCallTO) {
        return Mono.justOrEmpty(transportCallTO.getLocation()).flatMap(locationService::ensureResolvable)
                .flatMap(loc -> {
            transportCallTO.setLocationID(loc.getId());
            return transportCallService.create(MappingUtils.instanceFrom(transportCallTO, TransportCall::new, AbstractTransportCall.class));
        }).thenReturn(transportCallTO);
    }

    @Override
    public Mono<TransportCallTO> update(TransportCallTO transportCall) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public Mono<Void> delete(TransportCallTO transportCall) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Override
    public TransportCallTORepository getRepository() {
        return transportCallTORepository;
    }

    @Override
    public String getIdOfEntity(TransportCallTO entity) {
        return entity.getTransportCallID();
    }


    public ExtendedRequest<TransportCallTO> newExtendedRequest() {
        return new ExtendedRequest<>(extendedParameters, r2dbcDialect, TransportCallTO.class);
    }

}
