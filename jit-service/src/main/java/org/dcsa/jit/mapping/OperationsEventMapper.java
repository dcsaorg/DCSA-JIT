package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OperationsEventMapper {
  @Mapping(target = "eventCreatedDateTime", source = "createdDateTime")
  OperationsEventTO toTO(OperationsEvent operationsEvent);
}
