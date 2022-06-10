package org.dcsa.jit.persistence.repository;

import lombok.NonNull;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OperationsEventRepository
    extends JpaRepository<OperationsEvent, UUID>, JpaSpecificationExecutor<OperationsEvent> {
  @Override
  Page<OperationsEvent> findAll(Specification<OperationsEvent> spec, @NonNull Pageable pageable);
}
