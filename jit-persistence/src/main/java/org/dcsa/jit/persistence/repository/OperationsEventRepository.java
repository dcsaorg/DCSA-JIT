package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OperationsEventRepository
    extends JpaRepository<OperationsEvent, UUID>, JpaSpecificationExecutor<OperationsEvent> {
  @Override
//  @EntityGraph(value = "graph.vessels")
  List<OperationsEvent> findAll(Specification<OperationsEvent> spec);
}
