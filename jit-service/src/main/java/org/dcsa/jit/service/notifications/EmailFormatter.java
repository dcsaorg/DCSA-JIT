package org.dcsa.jit.service.notifications;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.service.notifications.model.FormattedEmail;
import org.dcsa.jit.service.notifications.model.MailConfiguration;
import org.dcsa.jit.service.notifications.model.MailTemplate;
import org.dcsa.jit.service.notifications.model.exceptions.UnknownTemplateKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class EmailFormatter {
  private static final Pattern TEMPLATE_PATTERN = Pattern.compile("[{][{]\\s*([a-zA-Z-_./]+)\\s*[}][}]");

  @Value("${dcsa.webui.baseUrl:NOT_SPECIFIED}")
  @Setter(AccessLevel.PACKAGE)
  private String webUIBaseUrl;

  private final MailConfiguration mailConfiguration;

  public FormattedEmail formatEmail(OperationsEvent operationsEvent, TimestampDefinition timestampDefinition, MailTemplate mailTemplate) {
    Map<String, Function<OperationsEvent, Object>> customValues = Map.of(
      "WEB_UI_BASE_URI", oe -> webUIBaseUrl,
      "TRANSPORT_CALL_ID", oe -> oe.getTransportCall().getId(),
      "TIMESTAMP_TYPE", oe -> timestampDefinition.getTimestampTypeName(),
      "VESSEL_NAME", oe -> oe.getTransportCall().getVessel().getName(),
      "VESSEL_IMO_NUMBER", oe -> oe.getTransportCall().getVessel().getVesselIMONumber()
    );

    TemplateSubst subst = TemplateSubst.of(
      mailTemplate.getTemplateName(),
      operationsEvent,
      mailConfiguration.getZoneId(),
      mailConfiguration.getDateFormat(),
      customValues
    );

    String subject = TEMPLATE_PATTERN.matcher(mailTemplate.getSubject()).replaceAll(subst);
    String body = TEMPLATE_PATTERN.matcher(mailTemplate.getBody()).replaceAll(subst);

    return new FormattedEmail(subject, body);
  }

  @RequiredArgsConstructor(staticName = "of")
  private static class TemplateSubst implements Function<MatchResult, String> {
    private final String templateName;
    private final OperationsEvent operationsEvent;
    private final ZoneId emailTimezone;
    private final String dateTimeFormat;
    private final Map<String, Function<OperationsEvent, Object>> substitutionValues;

    @Getter(lazy = true)
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

    public String apply(MatchResult matchResult) {
      String key = matchResult.group(1);
      Function<OperationsEvent, Object> valueFunction = substitutionValues.getOrDefault(key, oe -> { throw new UnknownTemplateKeyException(key, templateName); });
      Object value = valueFunction.apply(operationsEvent);
      if (value instanceof OffsetDateTime) {
        Instant instant = ((OffsetDateTime) value).toInstant();
        ZoneOffset offset = emailTimezone.getRules().getOffset(instant);
        OffsetDateTime timeAtOffset = instant.atOffset(offset);
        value = getFormatter().format(timeAtOffset);
      }
      return String.valueOf(value);
    }
  }
}
