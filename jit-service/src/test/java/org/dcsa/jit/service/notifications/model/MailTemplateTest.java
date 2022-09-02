package org.dcsa.jit.service.notifications.model;

import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.PublisherPattern;
import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;
import org.dcsa.jit.persistence.entity.enums.PublisherRole;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MailTemplateTest {
  @Test
  public void testEmptyEventClassifierCodeAndPrimaryReceiver() {
    MailTemplate mailTemplate = new MailTemplate();

    assertTrue(mailTemplate.appliesToEvent(OperationsEvent.builder().build(), TimestampDefinition.builder().build()));
  }

  @Test
  public void testEventClassifierCodeMatch() {
    MailTemplate mailTemplate = new MailTemplate();
    mailTemplate.setOnlyForEventClassifierCode(Set.of(EventClassifierCode.EST, EventClassifierCode.ACT));

    assertTrue(mailTemplate.appliesToEvent(operationsEvent(EventClassifierCode.EST), TimestampDefinition.builder().build()));
  }

  @Test
  public void testEventClassifierCodeDoesNotMatch() {
    MailTemplate mailTemplate = new MailTemplate();
    mailTemplate.setOnlyForEventClassifierCode(Set.of(EventClassifierCode.EST, EventClassifierCode.ACT));

    assertFalse(mailTemplate.appliesToEvent(operationsEvent(EventClassifierCode.PLN), TimestampDefinition.builder().build()));
  }

  @Test
  public void testPrimaryReceiverMatch() {
    MailTemplate mailTemplate = new MailTemplate();
    mailTemplate.setOnlyWhenPrimaryReceiverIs(Set.of(PublisherRole.CA, PublisherRole.AG));

    assertTrue(mailTemplate.appliesToEvent(OperationsEvent.builder().build(), timestampDefinition(PublisherRole.CA, PublisherRole.AG)));
  }

  @Test
  public void testPrimaryReceiverContainsAll() {
    MailTemplate mailTemplate = new MailTemplate();
    mailTemplate.setOnlyWhenPrimaryReceiverIs(Set.of(PublisherRole.CA, PublisherRole.AG, PublisherRole.VSL));

    assertTrue(mailTemplate.appliesToEvent(OperationsEvent.builder().build(), timestampDefinition(PublisherRole.CA, PublisherRole.AG)));
  }

  @Test
  public void testPrimaryReceiverContainsSome() {
    MailTemplate mailTemplate = new MailTemplate();
    mailTemplate.setOnlyWhenPrimaryReceiverIs(Set.of(PublisherRole.CA, PublisherRole.VSL));

    assertTrue(mailTemplate.appliesToEvent(OperationsEvent.builder().build(), timestampDefinition(PublisherRole.CA, PublisherRole.AG)));
  }

  @Test
  public void testPrimaryReceiverIsEmpty() {
    MailTemplate mailTemplate = new MailTemplate();
    mailTemplate.setOnlyWhenPrimaryReceiverIs(Set.of(PublisherRole.CA, PublisherRole.AG, PublisherRole.VSL));

    assertFalse(mailTemplate.appliesToEvent(OperationsEvent.builder().build(), timestampDefinition()));
  }

  private OperationsEvent operationsEvent(EventClassifierCode eventClassifierCode) {
    return OperationsEvent.builder()
      .eventClassifierCode(eventClassifierCode)
      .build();
  }

  private TimestampDefinition timestampDefinition(PublisherRole... primaryReceivers) {
    return TimestampDefinition.builder()
      .publisherPattern(Arrays.stream(primaryReceivers).map(role -> PublisherPattern.builder().primaryReceiver(role).build()).collect(Collectors.toSet()))
      .build();
  }
}

