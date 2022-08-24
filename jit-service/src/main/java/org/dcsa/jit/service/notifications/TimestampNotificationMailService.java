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
import org.dcsa.jit.service.notifications.model.FormattedEmail;
import org.dcsa.jit.service.notifications.model.MailConfiguration;
import org.dcsa.jit.service.notifications.model.MailTemplate;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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

  @Override
  public void configure() {
    from("jpa:org.dcsa.jit.persistence.entity.PendingEmailNotification?namedQuery=PendingEmailNotification.nextPendingEmailNotifications&delay=3600&transacted=true")
      .bean(this, "processEventMessage");
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
      log.debug("Email notifications are disabled for template {}", pendingEmailNotification.getTemplateName());
      return;
    }

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

    if (mailTemplate.appliesToEvent(operationsEvent, timestampDefinition)) {
      formatAndSendEmail(operationsEvent, timestampDefinition, pendingEmailNotification.getTemplateName(), mailTemplate);
    }
  }

  private void formatAndSendEmail(OperationsEvent operationsEvent, TimestampDefinition timestampDefinition, String templateName, MailTemplate mailTemplate) {
    FormattedEmail formattedEmail = emailFormatter.formatEmail(operationsEvent, timestampDefinition, mailTemplate);

    try {
      MimeMessage message = emailSender.createMimeMessage();
      message.setFrom(mailConfiguration.getFrom());
      message.setRecipients(Message.RecipientType.TO, mailTemplate.getTo());
      message.setSubject(formattedEmail.subject());
      message.setContent(formattedEmail.body(), "text/html");
      emailSender.send(message);
    } catch (MailAuthenticationException e) {
      log.warn("Failed to send mail as login towards the relay failed: " + e.toString(), e);
      log.warn("Please ensure that spring.mail.{host,username,password} + spring.mail.properties.mail.smtp.{port,auth} are set correctly.");
      //return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    } catch (MailException | MessagingException exception) {
      log.error("Fail to send email (" + templateName + ") " + mailTemplate.getTo() + ": " + exception.getLocalizedMessage(), exception);
      //return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    } catch (EmailFormatter.UnknownTemplateKeyException e) {
      log.error("Email template references unknown {{" + e.getMessage() + "}} in dcsa.email.templates." + templateName + ".body");
      //return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

  }
}
