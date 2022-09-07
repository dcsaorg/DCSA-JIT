package org.dcsa.jit.notifications;

import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.OpsEventTimestampDefinition;
import org.dcsa.jit.persistence.entity.PendingEmailNotification;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;
import org.dcsa.jit.persistence.repository.OperationsEventRepository;
import org.dcsa.jit.persistence.repository.OpsEventTimestampDefinitionRepository;
import org.dcsa.jit.persistence.repository.PendingEmailNotificationDeadRepository;
import org.dcsa.jit.persistence.repository.PendingEmailNotificationRepository;
import org.dcsa.jit.notifications.model.FormattedEmail;
import org.dcsa.jit.notifications.model.MailConfiguration;
import org.dcsa.jit.notifications.model.MailTemplate;
import org.dcsa.jit.notifications.model.exceptions.EntityNotFoundMailNotificationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.dcsa.jit.notifications.TimestampDefinitionDataFactory.timestampDefinition;
import static org.dcsa.jit.notifications.MailNotificationsOperationsEventDataFactory.operationsEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TimestampNotificationMailServiceTest {

  @Mock private OperationsEventRepository operationsEventRepository;
  @Mock private OpsEventTimestampDefinitionRepository opsEventTimestampDefinitionRepository;
  @Mock private MailConfiguration mailConfiguration;
  @Mock private EmailFormatter emailFormatter;
  @Mock private JavaMailSender emailSender;
  @Mock private PendingEmailNotificationRepository pendingEmailNotificationRepository;
  @Mock private PendingEmailNotificationDeadRepository pendingEmailNotificationDeadRepository;

  @InjectMocks private TimestampNotificationMailService timestampNotificationMailService;

  @Test
  @DisplayName("Test no attempt to load operations event or timestamp def when emails are disabled globally")
  public void testProcessEventMessageEmailsGloballyDisabledGlobally() {
    // Setup
    when(mailConfiguration.isEnableEmailNotifications()).thenReturn(false);

    // Execute
    timestampNotificationMailService.processEventMessage(PendingEmailNotification.builder().build());

    // Verify
    verify(mailConfiguration, never()).getTemplate(any());
    verify(operationsEventRepository, never()).findById(any());
    verify(opsEventTimestampDefinitionRepository, never()).findById(any());
  }

  @Test
  @DisplayName("Test no attempt to load operations event or timestamp def when emails are disabled for template")
  public void testProcessEventMessageEmailsGloballyDisabledForTemplate() {
    // Setup
    MailTemplate mailTemplate = mailTemplate();
    mailTemplate.setEnableEmailNotifications(false);
    when(mailConfiguration.isEnableEmailNotifications()).thenReturn(true);
    when(mailConfiguration.getTemplate(any())).thenReturn(mailTemplate);

    // Execute
    timestampNotificationMailService.processEventMessage(PendingEmailNotification.builder().build());

    // Verify
    verify(mailConfiguration).getTemplate(any());
    verify(operationsEventRepository, never()).findById(any());
    verify(opsEventTimestampDefinitionRepository, never()).findById(any());
  }

  @Test
  @DisplayName("Test error if operations event cannot be found")
  public void testProcessEventMessageEmailsNoOpsEvent() {
    // Setup
    UUID eventId = UUID.fromString("414f91c2-650c-4f73-82cb-bd1171296140");
    when(mailConfiguration.isEnableEmailNotifications()).thenReturn(true);
    when(mailConfiguration.getTemplate(any())).thenReturn(mailTemplate());
    when(operationsEventRepository.findById(eventId)).thenReturn(Optional.empty());

    // Execute
    EntityNotFoundMailNotificationException exception = assertThrows(EntityNotFoundMailNotificationException.class, () ->
      timestampNotificationMailService.processEventMessage(PendingEmailNotification.builder().eventID(eventId).build())
    );

    // Verify
    verify(operationsEventRepository).findById(eventId);
    assertEquals("No OperationsEvent with id = " + eventId, exception.getMessage());
  }

  @Test
  @DisplayName("Test error if timestamp definition cannot be found")
  public void testProcessEventMessageEmailsNoTimestampDef() {
    // Setup
    UUID eventId = UUID.fromString("414f91c2-650c-4f73-82cb-bd1171296140");
    when(mailConfiguration.isEnableEmailNotifications()).thenReturn(true);
    when(mailConfiguration.getTemplate(any())).thenReturn(mailTemplate());
    when(operationsEventRepository.findById(eventId)).thenReturn(Optional.of(operationsEvent()));
    when(opsEventTimestampDefinitionRepository.findById(eventId)).thenReturn(Optional.empty());

    // Execute
    EntityNotFoundMailNotificationException exception = assertThrows(EntityNotFoundMailNotificationException.class, () ->
      timestampNotificationMailService.processEventMessage(PendingEmailNotification.builder().eventID(eventId).build())
    );

    // Verify
    verify(operationsEventRepository).findById(eventId);
    assertEquals("No TimestampDefinition for OperationsEvent with id = " + eventId, exception.getMessage());
  }

  @Test
  @DisplayName("Test no email is sent if template does not apply")
  public void testProcessEventMessageTemplateDoesNotApply() {
    // Setup
    UUID eventId = UUID.fromString("414f91c2-650c-4f73-82cb-bd1171296140");
    MailTemplate mailTemplate = mailTemplate();
    mailTemplate.setOnlyForEventClassifierCode(Set.of(EventClassifierCode.EST));
    when(mailConfiguration.isEnableEmailNotifications()).thenReturn(true);
    when(mailConfiguration.getTemplate(any())).thenReturn(mailTemplate);
    when(operationsEventRepository.findById(eventId)).thenReturn(Optional.of(operationsEvent()));
    when(opsEventTimestampDefinitionRepository.findById(eventId)).thenReturn(Optional.of(opsEventTimestampDefinition()));

    // Execute
    timestampNotificationMailService.processEventMessage(PendingEmailNotification.builder().eventID(eventId).build());

    // Verify
    verify(emailFormatter, never()).formatEmail(any(), any(), any());
    verify(emailSender, never()).send(any(MimeMessage.class));
  }

  @Test
  @DisplayName("Test the happy case")
  public void testProcessEventMessageHappyCase() {
    // Setup
    UUID eventId = UUID.fromString("414f91c2-650c-4f73-82cb-bd1171296140");
    when(mailConfiguration.getFrom()).thenReturn("dummy-from@dcsa.org.invalid");
    when(mailConfiguration.isEnableEmailNotifications()).thenReturn(true);
    when(mailConfiguration.getTemplate(any())).thenReturn(mailTemplate());
    when(operationsEventRepository.findById(eventId)).thenReturn(Optional.of(operationsEvent()));
    when(opsEventTimestampDefinitionRepository.findById(eventId)).thenReturn(Optional.of(opsEventTimestampDefinition()));
    when(emailFormatter.formatEmail(any(), any(), any())).thenReturn(new FormattedEmail("subject", "body"));
    when(emailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

    // Execute
    timestampNotificationMailService.processEventMessage(PendingEmailNotification.builder().eventID(eventId).build());

    // Verify
    verify(emailFormatter).formatEmail(any(), any(), any());
    verify(emailSender).send(any(MimeMessage.class));
  }

  private MailTemplate mailTemplate() {
    MailTemplate mailTemplate = new MailTemplate();
    mailTemplate.setTemplateName("test-template");
    mailTemplate.setTo("dummy@dcsa.org.invalid");
    mailTemplate.setEnableEmailNotifications(true);
    mailTemplate.setSubject("Notification for {{VESSEL_NAME}}");
    mailTemplate.setBody("Stuff {{VESSEL_IMO_NUMBER}} Stuff {{VESSEL_IMO_NUMBER}} Stuff {{VESSEL_IMO_NUMBER}} Stuff {{VESSEL_IMO_NUMBER}} Stuff");
    return mailTemplate;
  }

  private OpsEventTimestampDefinition opsEventTimestampDefinition() {
    return OpsEventTimestampDefinition.builder()
      .timestampDefinition(timestampDefinition())
      .build();
  }
}
