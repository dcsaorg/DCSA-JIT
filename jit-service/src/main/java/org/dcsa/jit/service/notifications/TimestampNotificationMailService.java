package org.dcsa.jit.service.notifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.OpsEventTimestampDefinition;
import org.dcsa.jit.persistence.entity.PendingEmailNotification;
import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.persistence.repository.OperationsEventRepository;
import org.dcsa.jit.persistence.repository.OpsEventTimestampDefinitionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimestampNotificationMailService extends RouteBuilder {
  private final OperationsEventRepository operationsEventRepository;
  private final OpsEventTimestampDefinitionRepository opsEventTimestampDefinitionRepository;

  @Override
  public void configure() {
    from("jpa:org.dcsa.jit.persistence.entity.PendingEmailNotification?namedQuery=PendingEmailNotification.nextPendingEmailNotifications&delay=3600&transacted=true")
      .bean(this);
  }

  // Called by camel.
  public void processEventMessage(PendingEmailNotification pendingEmailNotification) {
    log.info("Processing message: {}", pendingEmailNotification);
    Optional<OperationsEvent> operationsEventOpt = operationsEventRepository.findById(pendingEmailNotification.getEventID());
    if (!operationsEventOpt.isPresent()) {
      log.warn("No OperationsEvent with id = {}", pendingEmailNotification.getEventID());
      return;
    }
    OperationsEvent operationsEvent = operationsEventOpt.get();
    log.info("Loaded OperationsEvent {}", operationsEvent);

    Optional<TimestampDefinition> timestampDefinitionOpt = opsEventTimestampDefinitionRepository.findById(pendingEmailNotification.getEventID())
      .map(OpsEventTimestampDefinition::getTimestampDefinition);
    if (!timestampDefinitionOpt.isPresent()) {
      log.warn("No TimestampDefinition for OperationsEvent with id = {}", pendingEmailNotification.getEventID());
      return;
    }
    TimestampDefinition timestampDefinition = timestampDefinitionOpt.get();
    log.info("Loaded TimestampDefinition {}", timestampDefinition);


    // TODO
  }
}
