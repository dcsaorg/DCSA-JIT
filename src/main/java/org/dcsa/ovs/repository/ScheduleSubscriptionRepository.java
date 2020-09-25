package org.dcsa.ovs.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.ovs.model.ScheduleSubscription;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ScheduleSubscriptionRepository extends ExtendedRepository<ScheduleSubscription, UUID> {


    @Query("SELECT callback_url FROM dcsa_ovs_v1_0.schedule_subscription " )
//            "            WHERE ((:startDate IS NULL or a.date_range =:startDate)" +
//
//
//            "    OR ((a.un_location_code='' OR a.un_location_code IS NULL)" +
//            "    AND (a.vessel_imo_number='' OR a.vessel_imo_number IS NULL)" +
//            "    AND (a.carrier_voyage_number='' OR a.carrier_voyage_number IS NULL)" +
//            "    AND (a.start_date IS NULL)" +
//            "    AND (a.carrier_service_code='' OR a.carrier_service_code IS NULL)" +
//            "    AND (a.date_range='' OR a.date_range IS NULL)))")
     Flux<String> getCallbackUrlsByFilters();

}
