package org.dcsa.ovs.repository;

import org.dcsa.ovs.model.TransportCall;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface TransportCallRepository extends R2dbcRepository<TransportCall, UUID> {

}
