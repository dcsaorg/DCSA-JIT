package org.dcsa.jit.service.notifications;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.OpsEventTimestampDefinition;
import org.dcsa.jit.persistence.entity.PendingEmailNotification;
import org.dcsa.jit.persistence.entity.PendingEmailNotificationDead;
import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.persistence.repository.OperationsEventRepository;
import org.dcsa.jit.persistence.repository.OpsEventTimestampDefinitionRepository;
import org.dcsa.jit.persistence.repository.PendingEmailNotificationDeadRepository;
import org.dcsa.jit.persistence.repository.PendingEmailNotificationRepository;
import org.dcsa.jit.service.notifications.model.FormattedEmail;
import org.dcsa.jit.service.notifications.model.MailConfiguration;
import org.dcsa.jit.service.notifications.model.MailTemplate;
import org.dcsa.jit.service.notifications.model.exceptions.EntityNotFoundMailNotificationException;
import org.dcsa.jit.service.notifications.model.exceptions.NonRecoverableMailNotificationException;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimestampNotificationMailService extends RouteBuilder {
  private final OperationsEventRepository operationsEventRepository;
  private final OpsEventTimestampDefinitionRepository opsEventTimestampDefinitionRepository;
  private final MailConfiguration mailConfiguration;
  private final EmailFormatter emailFormatter;
  private final JavaMailSender emailSender;
  private final PendingEmailNotificationRepository pendingEmailNotificationRepository;
  private final PendingEmailNotificationDeadRepository pendingEmailNotificationDeadRepository;

  @Override
  public void configure() {
    log.info("Configuring camel route with batchSize {} and delay {}", mailConfiguration.getBatchSize(), mailConfiguration.getDelay());
    from("jpa:org.dcsa.jit.persistence.entity.PendingEmailNotification?namedQuery=PendingEmailNotification.nextPendingEmailNotifications&delay="
      + mailConfiguration.getDelay() + "&maximumResults=" + mailConfiguration.getBatchSize())
      .bean(this, "processEventMessage")
      .onException(NonRecoverableMailNotificationException.class, MailAuthenticationException.class)
        .useOriginalMessage()
        .process(this::handleFailedEventMessage)
        .handled(true)
      .end()
      .onException(Exception.class)
        .useOriginalMessage()
        .maximumRedeliveries(mailConfiguration.getMaximumRedeliveries())
        .redeliveryDelay(mailConfiguration.getRedeliveryDelay())
        .process(this::handleFailedEventMessage)
        .handled(true)
      .end()
    ;
  }

  @Transactional
  public void enqueueEmailNotificationForEvent(OperationsEvent operationsEvent) {
    pendingEmailNotificationRepository.save(PendingEmailNotification.builder()
        .eventID(operationsEvent.getEventID())
        .templateName("timestampReceived")
        .enqueuedAt(OffsetDateTime.now())
        .build());
  }

  // Called by camel.
  public void processEventMessage(PendingEmailNotification pendingEmailNotification) {
    log.debug("Processing message: {}", pendingEmailNotification);

    if (!mailConfiguration.isEnableEmailNotifications()) {
      log.debug("Email notifications are globally disabled");
      return;
    }
    MailTemplate mailTemplate = mailConfiguration.getTemplate(pendingEmailNotification.getTemplateName());
    if (!mailTemplate.isEnableEmailNotifications()) {
      log.debug("Email notifications are disabled for template '{}'", pendingEmailNotification.getTemplateName());
      return;
    }

    Optional<OperationsEvent> operationsEventOpt = operationsEventRepository.findById(pendingEmailNotification.getEventID());
    if (!operationsEventOpt.isPresent()) {
      throw new EntityNotFoundMailNotificationException("No OperationsEvent with id = " + pendingEmailNotification.getEventID());
    }
    OperationsEvent operationsEvent = operationsEventOpt.get();
    log.debug("Loaded OperationsEvent {}", operationsEvent);

    Optional<TimestampDefinition> timestampDefinitionOpt = opsEventTimestampDefinitionRepository.findById(pendingEmailNotification.getEventID())
      .map(OpsEventTimestampDefinition::getTimestampDefinition);
    if (!timestampDefinitionOpt.isPresent()) {
      throw new EntityNotFoundMailNotificationException("No TimestampDefinition for OperationsEvent with id = " + pendingEmailNotification.getEventID());
    }
    TimestampDefinition timestampDefinition = timestampDefinitionOpt.get();
    log.debug("Loaded TimestampDefinition {}", timestampDefinition);

    if (mailTemplate.appliesToEvent(operationsEvent, timestampDefinition)) {
      formatAndSendEmail(operationsEvent, timestampDefinition, mailTemplate);
    }
  }

  @SneakyThrows
  private void formatAndSendEmail(OperationsEvent operationsEvent, TimestampDefinition timestampDefinition, MailTemplate mailTemplate) {
    FormattedEmail formattedEmail = emailFormatter.formatEmail(operationsEvent, timestampDefinition, mailTemplate);

    try {
      MimeMessage message = emailSender.createMimeMessage();
      message.setFrom(mailConfiguration.getFrom());
      message.setRecipients(Message.RecipientType.TO, mailTemplate.getTo());
      message.setSubject(formattedEmail.subject());
      message.setContent(formattedEmail.body(), "text/html");
      emailSender.send(message);
      log.info("Sent email '{}' for event {}", mailTemplate.getTemplateName(), operationsEvent.getEventID());
    } catch (MailAuthenticationException e) {
      log.warn("Failed to send mail as login towards the relay failed: {}", e.getMessage(), e);
      log.warn("Please ensure that spring.mail.{host,username,password} + spring.mail.properties.mail.smtp.{port,auth} are set correctly.");
      throw e;
    } catch (MailException | MessagingException e) {
      log.warn("Failed to send email ({}) to {}: {}", mailTemplate.getTemplateName(), mailTemplate.getTo(), e.getLocalizedMessage(), e);
      throw e;
    }
  }

  public void handleFailedEventMessage(Exchange exchange) {
    PendingEmailNotification pendingEmailNotification = exchange.getUnitOfWork().getOriginalInMessage().getBody(PendingEmailNotification.class);
    Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
    log.error("Processing dead message: {} -> {} '{}'", pendingEmailNotification, cause.getClass().getName(), cause.getMessage());
    pendingEmailNotificationDeadRepository.save(PendingEmailNotificationDead.from(pendingEmailNotification, cause));
  }
}
