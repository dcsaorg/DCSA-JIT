package org.dcsa.ovs.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.ovs.model.Facility;
import org.dcsa.ovs.model.TransportCall;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransportCallRepository extends ExtendedRepository<TransportCall, String> {

    @Query("SELECT transport_call FROM dcsa_im_v3_0.transport_call WHERE transport_call.id = :transportCallId AND transport_call.facility_id = :facilityId")
    Mono<TransportCall> getTransportCall(String transportCallId, String facilityId);
}
