package org.dcsa.jit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.persistence.entity.MessageRoutingRule;
import org.dcsa.jit.persistence.entity.OutboxMessage;
import org.dcsa.jit.persistence.repository.MessageRoutingRuleRepository;
import org.dcsa.jit.persistence.repository.OutboxMessageRepository;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TimestampRoutingService {
  private final MessageRoutingRuleRepository messageRoutingRuleRepository;
  private final OutboxMessageRepository outboxMessageRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  @SneakyThrows // JsonProcessingException when serializing timestamp
  public void routeMessage(TimestampTO timestamp) {
    String vesselIMONumber = timestamp.canonicalVesselIMONumber();
    List<MessageRoutingRule> messageRoutingRules = messageRoutingRuleRepository.findRulesMatchingVesselIMONumber(vesselIMONumber);
    if (!messageRoutingRules.isEmpty()) {
      String payload = objectMapper.writeValueAsString(timestamp);
      List<OutboxMessage> outboxMessages = messageRoutingRules.stream()
        .map(rule -> toOutboxMessage(rule, payload))
        .toList();
      outboxMessageRepository.saveAll(outboxMessages);
    } else {
      log.debug("No message routing rules found for vesselIMONumber '{}'", vesselIMONumber);
    }
  }

  private OutboxMessage toOutboxMessage(MessageRoutingRule rule, String payload) {
    return OutboxMessage.builder()
      .messageRoutingRule(rule)
      .payload(payload)
      .build();
  }
}
