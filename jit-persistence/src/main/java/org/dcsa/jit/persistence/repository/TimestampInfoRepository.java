package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.TimestampInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface TimestampInfoRepository
    extends JpaRepository<TimestampInfo, UUID>,
            JpaSpecificationExecutor<TimestampInfo> {

  @EntityGraph(value = "timestampInfo.allAttributes")
  List<TimestampInfo> findAll(Sort sort);

  @EntityGraph(value = "timestampInfo.allAttributes")
  Page<TimestampInfo> findAll(Specification<TimestampInfo> spec, Pageable pageable);

}
