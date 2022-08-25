package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.SMDGDelayReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SMDGDelayReasonRepository extends JpaRepository<SMDGDelayReason, String> {}
