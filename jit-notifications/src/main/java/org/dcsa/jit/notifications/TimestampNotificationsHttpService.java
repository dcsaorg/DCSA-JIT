package org.dcsa.jit.notifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.dcsa.jit.persistence.entity.OutboxMessage;
import org.dcsa.jit.persistence.entity.TimestampNotificationDead;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class TimestampNotificationsHttpService extends RouteBuilder {
  @Override
  public void configure() {
    pollOutstandingRoutingMessages();
    emitMessage();
    tokenOIDC();
    timestampNotification();
  }

  void pollOutstandingRoutingMessages() {
    from("{{camel.route.outbox-msg}}")
      .routeId("outbox-message-poll")
      .errorHandler(noErrorHandler())
      .to("{{camel.route.emit-message}}");
  }

  void emitMessage() {
    from("{{camel.route.emit-message}}")
      .routeId("emit-msg")
      .errorHandler(noErrorHandler())
      .log("processing msg: ${body.payload}")
      .setProperty("outboxMessage", simple("${body}"))
      .choice()
      // outbox_message has a not null constraint on message_routing_rule_id, so it is always
      // present
      // hence a check for null is not required
      .when(simple("${body.messageRoutingRule.loginType} == 'OIDC'"))
      .setProperty("credentials", simple("${body.messageRoutingRule.loginInformation}"))
      .to("{{camel.route.oidc}}")
      .otherwise()
      .log("Unhandled loginType detected ${body.messageRoutingRule.loginType}")
      .stop()
      .end()
      .to("{{camel.route.timestamp-notify}}");
  }

  void tokenOIDC() {
    from("{{camel.route.oidc}}")
      .routeId("oidc")
      .onException(HttpOperationFailedException.class)
      .continued(true)
      .maximumRedeliveries("{{camel.max-redeliveries}}")
      .redeliveryDelay("{{camel.redelivery-delay}}")
      .useExponentialBackOff()
      .backOffMultiplier(2)
      .log(LoggingLevel.WARN, "Auth token request failed!")
      .process(new DefaultTokenProcessor())
      .marshal()
      .json(JsonLibrary.Jackson)
      .end()
      .setHeader("CamelHttpMethod", constant("POST"))
      .setHeader("Content-Type", simple("application/x-www-form-urlencoded"))
      .setBody(
        simple(
          "grant_type=client_credentials&client_id=${exchangeProperty.credentials.clientID}&client_secret=${exchangeProperty.credentials.clientSecret}"))
      .toD("${exchangeProperty.credentials.tokenURL}")
      .log("auth response : ${body}")
      .unmarshal()
      .allowNullBody()
      .json(JsonLibrary.Jackson)
      .transform(simple("Bearer ${body[access_token]}"));
  }

  static class DefaultTokenProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
      HashMap<String, String> tokenMap = new HashMap<>();
      tokenMap.put("access_token", "<token>");
      exchange.getIn().setBody(tokenMap);
    }
  }

  void timestampNotification() {
    from("{{camel.route.timestamp-notify}}")
      .routeId("timestamp-notify")
      .onException(HttpOperationFailedException.class)
      .handled(true)
      .maximumRedeliveries("{{camel.max-redeliveries}}")
      .redeliveryDelay("{{camel.redelivery-delay}}")
      .useExponentialBackOff()
      .backOffMultiplier(2)
      .process(new DeadTimestampProcessor())
      .to("jpa:org.dcsa.jit.persistence.entity.TimestampNotificationDead")
      .end()
      .setHeader("CamelHttpMethod", constant("POST"))
      .setHeader("Content-Type", simple("application/json"))
      .setHeader("Authorization", simple("${body}"))
      .setBody(simple("${exchangeProperty.outboxMessage.payload}"))
      .toD("${exchangeProperty.outboxMessage.messageRoutingRule.apiUrl}");
  }

  // inner class as its only required within TimestampRoutingService
  static class DeadTimestampProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
      OutboxMessage outboxMessage = (OutboxMessage) exchange.getProperty("outboxMessage");
      Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
      log.info("Failed to delivery timestamp: {} -> {} '{}'", outboxMessage, cause.getClass().getName(), cause.getMessage());
      exchange
        .getIn()
        .setBody(TimestampNotificationDead.from(outboxMessage));
    }
  }
}
