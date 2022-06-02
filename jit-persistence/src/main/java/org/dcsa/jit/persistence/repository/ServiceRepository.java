package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceRepository extends JpaRepository<Service, UUID> {}
