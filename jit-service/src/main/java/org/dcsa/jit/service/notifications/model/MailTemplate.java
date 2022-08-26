package org.dcsa.jit.service.notifications.model;

import lombok.Data;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.Set;

@Data
public class MailTemplate {

  @Email
  private String to = "NOT_SPECIFIED";

  @NotBlank
  private String subject;

  @NotBlank
  private String body;

  // Weather to send email notifications that are using this template
  private boolean enableEmailNotifications = true;

  private Set<EventClassifierCode> onlyForEventClassifierCode = Collections.emptySet();

  public boolean appliesToEvent(OperationsEvent event) {
    return onlyForEventClassifierCode.isEmpty() || onlyForEventClassifierCode.contains(event.getEventClassifierCode());
  }
}
