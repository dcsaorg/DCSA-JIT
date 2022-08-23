package org.dcsa.jit.service.notifications.model;

import lombok.Data;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;
import org.dcsa.jit.persistence.entity.enums.PartyFunction;

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

  private Set<EventClassifierCode> onlyForEventClassifierCode = Collections.emptySet();

  private Set<EventType> onlyForEventType = Collections.emptySet(); // TODO

  private Set<PartyFunction> onlyWhenPrimaryReceiverIs = Collections.emptySet();

  public boolean appliesToEvent(OperationsEvent event, TimestampDefinition timestampDefinition) {
    //if (!onlyForEventType.isEmpty() && !onlyForEventType.contains(event.getEventType())) {
    //  return false;
    //}
    if (!onlyForEventClassifierCode.isEmpty() && !onlyForEventClassifierCode.contains(event.getEventClassifierCode())) {
      return false;
    }
    //if(!onlyWhenPrimaryReceiverIs.isEmpty() && (timestampDefinition == null || !onlyWhenPrimaryReceiverIs.contains(timestampDefinition.getPrimaryReceiver()))) {
    //  return false;
    //}
    return true;
  }

}
