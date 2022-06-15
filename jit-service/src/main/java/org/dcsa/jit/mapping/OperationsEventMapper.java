package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {
      TransportCallMapper.class,
      PartyMapper.class,
      AddressMapper.class
    })
public interface OperationsEventMapper {
  OperationsEventTO toTO(OperationsEvent operationsEvent);
}
