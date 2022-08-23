package org.dcsa.jit.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.entity.PendingEmailNotification;
import org.dcsa.jit.persistence.repository.PendingEmailNotificationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// TODO - delete me
@RestController
@RequiredArgsConstructor
public class TestController {
  private final PendingEmailNotificationRepository pendingEmailNotificationRepository;

  @GetMapping(path = "/pending-emails")
  public List<PendingEmailNotification> get() {
    return pendingEmailNotificationRepository.nextPendingEmailNotifications();
  }
}
