package org.dcsa.ovs.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import org.dcsa.core.events.model.Transport;

import java.util.UUID;

public interface TransportRepository extends ExtendedRepository<Transport, UUID> {

    @Query("SELECT transport FROM transport WHERE transport.un_location_code =:modeOfTransport AND transport.vessel_imo_number = :vesselIMONumber")
    Flux<Transport> getTransports(String modeOfTransport, String vesselIMONumber);
}
