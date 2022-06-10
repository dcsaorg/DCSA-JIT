package org.dcsa.jit.persistence.repository;

import org.dcsa.skernel.domain.persistence.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {}
