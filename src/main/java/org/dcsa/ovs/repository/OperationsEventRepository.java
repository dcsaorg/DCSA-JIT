package org.dcsa.ovs.repository;

import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.repository.ExtendedRepository;


import java.util.UUID;

public interface OperationsEventRepository extends ExtendedRepository<OperationsEvent, UUID> {
}
