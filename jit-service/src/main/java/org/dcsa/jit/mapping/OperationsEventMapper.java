package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.transferobjects.LocationTO;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OperationsEventMapper {
  OperationsEventTO toTO(OperationsEvent operationsEvent);
}
