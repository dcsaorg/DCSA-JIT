package org.dcsa.jit.service;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Service;

@Service
public class TimestampNotificationMailService extends RouteBuilder {
  @Override
  public void configure() {
    from("jpa:org.dcsa.jit.persistence.entity.PendingEmailNotification?namedQuery=PendingEmailNotification.nextPendingEmailNotifications&delay=3600&transacted=true")
    .to("direct:PendingEmailNotification");

    from("direct:PendingEmailNotification")
      .log("Message received");
  }
}
