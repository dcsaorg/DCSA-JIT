package org.dcsa.ovs.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.ovs.model.Carrier;
import org.dcsa.ovs.model.Facility;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FacilityRepository extends ExtendedRepository<Facility, UUID> {

    @Query("SELECT facility FROM dcsa_im_v3_0.facility WHERE facility.un_location_code =:unLocationCode AND facility.facility_smdg_code = :smdgCode")
    Flux<Facility> getFacilities(String unLocationCode, String smdgCode);

    Mono<Facility> findByUnLocationCodeAndFacilitySMGDCode(String UNLocationCode, String facilitySMDGCode);
    Mono<Facility> findByUnLocationCodeAndFacilityBICCode(String UNLocationCode, String facilityBICCode);
}
