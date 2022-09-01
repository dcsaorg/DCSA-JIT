package org.dcsa.jit.service.notifications;

import org.dcsa.jit.service.notifications.model.FormattedEmail;
import org.dcsa.jit.service.notifications.model.MailConfiguration;
import org.dcsa.jit.service.notifications.model.MailTemplate;
import org.dcsa.jit.service.notifications.model.exceptions.UnknownTemplateKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;

import static org.dcsa.jit.service.datafactories.TimestampDefinitionDataFactory.timestampDefinition;
import static org.dcsa.jit.service.notifications.MailNotificationsOperationsEventDataFactory.operationsEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailFormatterTest {

  @Mock private MailConfiguration mailConfiguration;
  @InjectMocks private EmailFormatter emailFormatter;

  @BeforeEach
  public void setup() {
    emailFormatter.setWebUIBaseUrl("WebUIBaseUrl");
    when(mailConfiguration.getZoneId()).thenReturn(ZoneId.of("Europe/Copenhagen"));
    when(mailConfiguration.getDateFormat()).thenReturn("MM/LLL/yyyy HH:mm");
  }

  @Test
  @DisplayName("Test substitution of all values")
  public void testAllValuesSubstituted() {
    // Setup
    MailTemplate mailTemplate = new MailTemplate();
    mailTemplate.setSubject("Notification for {{VESSEL_NAME}}");
    mailTemplate.setBody("Stuff {{WEB_UI_BASE_URI}} Stuff {{TRANSPORT_CALL_ID}} Stuff {{PORT_VISIT_ID}} Stuff {{TIMESTAMP_TYPE}} Stuff {{VESSEL_IMO_NUMBER}} Stuff");

    // Execute
    FormattedEmail formattedEmail = emailFormatter.formatEmail(operationsEvent(), timestampDefinition(), mailTemplate);

    // Verify
    assertEquals("Notification for my-vessel-name", formattedEmail.subject());
    assertEquals("Stuff WebUIBaseUrl Stuff 414f91c2-650c-4f73-82cb-bd1171296140 Stuff 5db49152-a0d3-41c6-af1d-219c0eb7abcd Stuff ETA-Berth Stuff 1234567 Stuff", formattedEmail.body());
  }

  @Test
  @DisplayName("Test multiple instances of the same variable are substituted")
  public void testSameVariableMultipleTimesSubstituted() {
    // Setup
    MailTemplate mailTemplate = new MailTemplate();
    mailTemplate.setSubject("Notification for {{VESSEL_NAME}}");
    mailTemplate.setBody("Stuff {{VESSEL_IMO_NUMBER}} Stuff {{VESSEL_IMO_NUMBER}} Stuff {{VESSEL_IMO_NUMBER}} Stuff {{VESSEL_IMO_NUMBER}} Stuff");

    // Execute
    FormattedEmail formattedEmail = emailFormatter.formatEmail(operationsEvent(), timestampDefinition(), mailTemplate);

    // Verify
    assertEquals("Notification for my-vessel-name", formattedEmail.subject());
    assertEquals("Stuff 1234567 Stuff 1234567 Stuff 1234567 Stuff 1234567 Stuff", formattedEmail.body());
  }

  @Test
  @DisplayName("Test exception is thrown on unknown variable in template")
  public void testUnknownVariable() {
    // Setup
    MailTemplate mailTemplate = new MailTemplate();
    mailTemplate.setTemplateName("my-template");
    mailTemplate.setSubject("Notification for {{UNDEFINED_VARIABLE}}");
    mailTemplate.setBody("Stuff {{VESSEL_IMO_NUMBER}} Stuff {{VESSEL_IMO_NUMBER}} Stuff {{VESSEL_IMO_NUMBER}} Stuff {{VESSEL_IMO_NUMBER}} Stuff");

    // Execute
    UnknownTemplateKeyException exception = assertThrows(UnknownTemplateKeyException.class, () ->
      emailFormatter.formatEmail(operationsEvent(), timestampDefinition(), mailTemplate)
    );

    // Verify
    String msg = "Email template references unknown key 'UNDEFINED_VARIABLE' in dcsa.email.templates.my-template.body";
    assertEquals(msg, exception.getMessage());
  }
}
