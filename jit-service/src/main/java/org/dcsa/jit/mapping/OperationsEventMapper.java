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
      AddressMapper.class,
      LocationMapper.class
    })
public interface OperationsEventMapper {
  @Mapping(target = "milesRemainingToDestination", source = "milesRemainingToDestination")
  @Mapping(target = "milesToDestinationPort", source = "milesRemainingToDestination")
  OperationsEventTO toTO(OperationsEvent operationsEvent);
}
