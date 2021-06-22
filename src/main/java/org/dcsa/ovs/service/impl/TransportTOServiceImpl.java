package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.model.transferobjects.TransportTO;
import org.dcsa.ovs.repository.TransportTORepository;
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

@RequiredArgsConstructor
@Service
public class TransportTOServiceImpl extends ExtendedBaseServiceImpl<TransportTORepository, TransportTO, UUID> implements TransportTOService {

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
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
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
