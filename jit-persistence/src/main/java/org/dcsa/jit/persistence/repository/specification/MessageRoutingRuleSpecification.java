package org.dcsa.jit.persistence.repository.specification;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.dcsa.jit.persistence.entity.MessageRoutingRule;
import org.dcsa.jit.persistence.entity.MessageRoutingRule_;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageRoutingRuleSpecification {

  @Builder
  public static class MessageRoutingRuleFilter {
    String publisherRole;
    String vesselIMONumber;
  }

  public static Specification<MessageRoutingRule> withFilters(final MessageRoutingRuleFilter filters) {
    return (root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (null != filters.publisherRole) {
        Predicate predicate = builder.equal(root.get(MessageRoutingRule_.PUBLISHER_ROLE), filters.publisherRole);
        predicates.add(predicate);
      }

      if (null != filters.vesselIMONumber) {
        Predicate predicate = builder.equal(root.get(MessageRoutingRule_.VESSEL_IM_ONUMBER), filters.vesselIMONumber);
        predicates.add(predicate);
      }

      return builder.and(predicates.toArray(Predicate[]::new));
    };
  }
}
