package org.dcsa.jit.notifications.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.PublisherPattern;
import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;
import org.dcsa.jit.persistence.entity.enums.OperationsEventTypeCode;
import org.dcsa.jit.persistence.entity.enums.PublisherRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.Set;

@Slf4j
@Data
public class MailTemplate {

  @Email
  private String to = "NOT_SPECIFIED";

  @NotBlank
  private String subject;

  @NotBlank
  private String body;

  private Set<EventClassifierCode> onlyForEventClassifierCode = Collections.emptySet();
  private Set<OperationsEventTypeCode> onlyWhenOperationsEventTypeCode = Collections.emptySet();
  private Set<PublisherRole> onlyWhenPrimaryReceiverIs = Collections.emptySet();

  // Weather to send email notifications that are using this template (set when validating templates)
  private boolean enableEmailNotifications = true;
  // Set when validating templates
  private String templateName;

  public boolean appliesToEvent(OperationsEvent event, TimestampDefinition timestampDefinition) {
    if (!onlyForEventClassifierCode.isEmpty() && !onlyForEventClassifierCode.contains(event.getEventClassifierCode())) {
      log.info("Template '{}' does not apply to event '{}' since event classifier code does not match", templateName, event.getEventID());
      return false;
    }
    if (!onlyWhenOperationsEventTypeCode.isEmpty() && !onlyWhenOperationsEventTypeCode.contains(event.getOperationsEventTypeCode())) {
      log.info("Template '{}' does not apply to event '{}' since event operations event type code does not match", templateName, event.getEventID());
      return false;
    }
    if (!onlyWhenPrimaryReceiverIs.isEmpty() &&
      timestampDefinition.getPublisherPattern().stream().map(PublisherPattern::getPrimaryReceiver).noneMatch(onlyWhenPrimaryReceiverIs::contains)
    ) {
      log.info("Template '{}' does not apply to event '{}' since primary receiver does not match", templateName, event.getEventID());
      return false;
    }
    return true;
  }
}
