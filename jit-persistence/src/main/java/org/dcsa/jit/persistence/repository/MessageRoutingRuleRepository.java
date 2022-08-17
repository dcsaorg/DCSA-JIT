package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.MessageRoutingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRoutingRuleRepository extends JpaRepository<MessageRoutingRule, UUID> {
  @Query("FROM MessageRoutingRule WHERE vesselIMONumber = :vesselIMONumber OR vesselIMONumber IS NULL")
  List<MessageRoutingRule> findRulesMatchingVesselIMONumber(String vesselIMONumber);
}
