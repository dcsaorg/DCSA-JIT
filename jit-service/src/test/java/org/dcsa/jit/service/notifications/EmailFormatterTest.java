package org.dcsa.jit.service.notifications;

import org.dcsa.jit.service.notifications.model.FormattedEmail;
import org.dcsa.jit.service.notifications.model.MailConfiguration;
import org.dcsa.jit.service.notifications.model.MailTemplate;
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
    mailTemplate.setBody("Stuff {{WEB_UI_BASE_URI}} Stuff {{TRANSPORT_CALL_ID}} Stuff {{TIMESTAMP_TYPE}} Stuff {{VESSEL_IMO_NUMBER}} Stuff");

    // Execute
    FormattedEmail formattedEmail = emailFormatter.formatEmail(operationsEvent(), timestampDefinition(), mailTemplate);

    // Verify
    assertEquals("Notification for my-vessel-name", formattedEmail.subject());
    assertEquals("Stuff WebUIBaseUrl Stuff 414f91c2-650c-4f73-82cb-bd1171296140 Stuff ETA-Berth Stuff 1234567 Stuff", formattedEmail.body());
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
}
