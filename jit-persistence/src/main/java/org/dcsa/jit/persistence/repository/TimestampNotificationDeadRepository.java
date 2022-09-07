package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.TimestampNotificationDead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TimestampNotificationDeadRepository
    extends JpaRepository<TimestampNotificationDead, UUID> {}
