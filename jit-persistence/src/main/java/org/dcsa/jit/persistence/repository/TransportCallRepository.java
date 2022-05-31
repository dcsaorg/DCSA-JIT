package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.TransportCall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransportCallRepository extends JpaRepository<TransportCall, UUID> {}
