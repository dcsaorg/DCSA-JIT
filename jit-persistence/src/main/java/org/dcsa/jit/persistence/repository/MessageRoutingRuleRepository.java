package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.MessageRoutingRule;
import org.dcsa.jit.persistence.entity.enums.PublisherRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRoutingRuleRepository extends JpaRepository<MessageRoutingRule, UUID>, JpaSpecificationExecutor<MessageRoutingRule> {
  @Query("""
           FROM MessageRoutingRule
          WHERE (vesselIMONumber = :vesselIMONumber OR vesselIMONumber IS NULL)
            AND (publisherRole = :publisherRole OR publisherRole IS NULL)
          """)
  List<MessageRoutingRule> findRulesMatchingVesselIMONumberAndPublisherRole(String vesselIMONumber, PublisherRole publisherRole);
}
