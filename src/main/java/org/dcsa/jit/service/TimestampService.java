package org.dcsa.jit.service;

import org.dcsa.jit.model.Timestamp;
import reactor.core.publisher.Mono;

public interface TimestampService {

    Mono<Timestamp> create(Timestamp t, byte[] originalPayload);
}