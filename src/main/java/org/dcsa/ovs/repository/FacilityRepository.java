package org.dcsa.ovs.repository;

import org.dcsa.core.events.model.Facility;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FacilityRepository extends ExtendedRepository<Facility, UUID> {

    @Query("SELECT facility FROM facility WHERE facility.un_location_code =:unLocationCode AND facility.facility_smdg_code = :smdgCode")
    Mono<Facility> getFacilities(String unLocationCode, String smdgCode);

    Mono<Facility> findByUnLocationCodeAndFacilitySMGDCode(String UNLocationCode, String facilitySMDGCode);
    Mono<Facility> findByUnLocationCodeAndFacilityBICCode(String UNLocationCode, String facilityBICCode);
}
