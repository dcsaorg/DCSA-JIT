package org.dcsa.jit.service.notifications.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Map;
import java.util.TimeZone;

@Data
@Slf4j
@ConfigurationProperties(prefix = "dcsa.email")
@Component
public class MailConfiguration {
  @Email
  private String from = "NOT_SPECIFIED";
  private String timezone = "NOT_SPECIFIED";
  private String dateFormat = "NOT_SPECIFIED";
  private Integer batchSize = 10;
  private Long delay = 3000L;
  private Integer maximumRedeliveries = 3;
  private Long redeliveryDelay = 10000L;

  // Weather to send email notifications at all
  private boolean enableEmailNotifications = true;
  private ZoneId zoneId;

  @Valid
  private Map<String, MailTemplate> templates;

  public MailTemplate getTemplate(String templateName) {
    MailTemplate template = templates.get(templateName);
    if (template == null) {
      throw new IllegalArgumentException("Unknown template: " + templateName);
    }
    return template;
  }

  // Validate configuration and provide log messages to aid with debugging.
  @EventListener(ApplicationStartedEvent.class)
  void validateConfiguration() {
    if ("NOT_SPECIFIED".equals(from)) {
      log.info("No from address specified (dcsa.email.from), disabling mail notifications");
      enableEmailNotifications = false;
      return;
    }
    TimeZone tz;
    if ("NOT_SPECIFIED".equals(timezone)) {
      tz = TimeZone.getDefault();
      log.info("No timezone specified (dcsa.email.timezone), email notifications will use " + tz.getID() + " - "
        + tz.getDisplayName());
    } else {
      try {
        zoneId = ZoneId.of(timezone);
      } catch (DateTimeException e) {
        tz = TimeZone.getDefault();
        log.warn("Please check the dcsa.email.timezone value in application.yaml for errors (e.g. typos)");
        log.info("Valid standard examples include: Etc/UTC or " + tz.getID());
        log.info("It is also possible to set it to SYSTEM_TIMEZONE.");
        log.info("For this system, SYSTEM_TIMEZONE resolves to: " + tz.getID() + " (" + tz.getDisplayName() + ")");
        log.info("Note that the SYSTEM_TIMEZONE does not always use the right city name as multiple cities use the same timezone");
        log.info("For more timezones, please have a look at: https://en.wikipedia.org/wiki/List_of_tz_database_time_zones");
        log.error("Invalid or unknown timezone identifier provided: " + timezone);
        throw e;
      }
      tz = TimeZone.getTimeZone(zoneId);

      log.info("Using timezone " + tz.getID() + " - " + tz.getDisplayName() + " for emails (resolved from dcsa.email.timezone=" + timezone + ")");
      log.info("If the timezone is wrong, then please set dcsa.email.timezone to the desired timezone");
    }
    for (Map.Entry<String, MailTemplate> templateEntry : templates.entrySet()) {
      String templateName = templateEntry.getKey();
      MailTemplate template = templateEntry.getValue();
      template.setTemplateName(templateName);
      String toAddress = template.getTo();
      if ("NOT_SPECIFIED".equals(toAddress)) {
        log.info("Disabled email notification for template " + templateName + " as it has no receiver (dcsa.email.templates." + templateName + ".to)");
        template.setEnableEmailNotifications(false);
      } else {
        log.info("Will send email notifications to " + toAddress + " for mail template (dcsa.email.templates." + templateName + ".to)");
        template.setEnableEmailNotifications(true);
      }
    }
  }
}
