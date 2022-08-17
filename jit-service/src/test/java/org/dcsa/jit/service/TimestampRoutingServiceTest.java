package org.dcsa.jit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dcsa.jit.persistence.entity.MessageRoutingRule;
import org.dcsa.jit.persistence.entity.OutboxMessage;
import org.dcsa.jit.persistence.repository.MessageRoutingRuleRepository;
import org.dcsa.jit.persistence.repository.OutboxMessageRepository;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.dcsa.jit.transferobjects.TimestampVesselTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TimestampRoutingServiceTest {

  @Mock private MessageRoutingRuleRepository messageRoutingRuleRepository;
  @Mock private OutboxMessageRepository outboxMessageRepository;
  @Mock private ObjectMapper objectMapper;

  @InjectMocks private TimestampRoutingService timestampRoutingService;

  @Test
  @DisplayName("Test that no OutboxMessages are persisted if there are no matching rules")
  public void testNoRules() throws Exception {
    // Setup
    TimestampTO timestamp = TimestampTO.builder()
      .vessel(TimestampVesselTO.builder().vesselIMONumber("stuff").build())
      .build();
    when(messageRoutingRuleRepository.findRulesMatchingVesselIMONumber(anyString())).thenReturn(Collections.emptyList());

    // Execute
    timestampRoutingService.routeMessage(timestamp);

    // Verify
    verify(objectMapper, never()).writeValueAsString(any());
    verify(outboxMessageRepository, never()).saveAll(any());
  }

  @Test
  @DisplayName("Test that an OutboxMessages consist of the matching rule and the serialized timestamp")
  public void testWithOneRule() throws Exception {
    // Setup
    TimestampTO timestamp = TimestampTO.builder()
      .vessel(TimestampVesselTO.builder().vesselIMONumber("stuff").build())
      .build();
    MessageRoutingRule messageRoutingRule = MessageRoutingRule.builder().id(UUID.randomUUID()).build();
    when(messageRoutingRuleRepository.findRulesMatchingVesselIMONumber(anyString())).thenReturn(List.of(messageRoutingRule));
    when(objectMapper.writeValueAsString(any(TimestampTO.class))).thenReturn("serialized timestamp");

    // Execute
    timestampRoutingService.routeMessage(timestamp);

    // Verify
    ArgumentCaptor<List<OutboxMessage>> messageCaptor = ArgumentCaptor.forClass(List.class);
    verify(objectMapper).writeValueAsString(any());
    verify(outboxMessageRepository).saveAll(messageCaptor.capture());

    List<OutboxMessage> outboxMessages = messageCaptor.getValue();
    assertEquals(1, outboxMessages.size());

    OutboxMessage outboxMessage = outboxMessages.get(0);
    assertSame(messageRoutingRule, outboxMessage.getMessageRoutingRule());
    assertEquals("serialized timestamp", outboxMessage.getPayload());
  }

  @Test
  @DisplayName("Test that the number of OutboxMessages matches the number of matching rules")
  public void testWithMultipleRules() throws Exception {
    // Setup
    TimestampTO timestamp = TimestampTO.builder()
      .vessel(TimestampVesselTO.builder().vesselIMONumber("stuff").build())
      .build();
    List<MessageRoutingRule> messageRoutingRules = List.of(
      MessageRoutingRule.builder().id(UUID.randomUUID()).build(),
      MessageRoutingRule.builder().id(UUID.randomUUID()).build(),
      MessageRoutingRule.builder().id(UUID.randomUUID()).build()
    );
    when(messageRoutingRuleRepository.findRulesMatchingVesselIMONumber(anyString())).thenReturn(messageRoutingRules);

    // Execute
    timestampRoutingService.routeMessage(timestamp);

    // Verify
    ArgumentCaptor<List<OutboxMessage>> messageCaptor = ArgumentCaptor.forClass(List.class);
    verify(objectMapper).writeValueAsString(any());
    verify(outboxMessageRepository).saveAll(messageCaptor.capture());

    List<OutboxMessage> outboxMessages = messageCaptor.getValue();
    assertEquals(messageRoutingRules.size(), outboxMessages.size());
  }
}
