package org.dcsa.jit.service.notifications.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "dcsa.email")
@Component
public class MailConfiguration {
  @Email
  private String from = "NOT_SPECIFIED";
  private String timezone = "NOT_SPECIFIED";
  private String dateFormat = "NOT_SPECIFIED";
  private boolean debugEmail = false;

  @Valid
  private Map<String, MailTemplate> templates;

  public MailTemplate getTemplate(String templateName) {
    MailTemplate template = templates.get(templateName);
    if (template == null) {
      throw new IllegalArgumentException("Unknown template: " + templateName);
    }
    return template;
  }
}
