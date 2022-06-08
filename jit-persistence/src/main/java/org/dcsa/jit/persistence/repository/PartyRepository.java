package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PartyRepository extends JpaRepository<Party, UUID> {}
