package org.dcsa.ovs.repository;

import org.dcsa.ovs.model.Schedule;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface ScheduleRepository extends R2dbcRepository<Schedule, UUID> {

}
