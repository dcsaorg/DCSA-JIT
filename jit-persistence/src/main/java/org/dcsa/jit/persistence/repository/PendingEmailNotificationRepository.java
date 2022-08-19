package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.PendingEmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PendingEmailNotificationRepository extends JpaRepository<PendingEmailNotification, UUID> {
  // Matches named query on PendingEmailNotification
  List<PendingEmailNotification> nextPendingEmailNotifications();
}
