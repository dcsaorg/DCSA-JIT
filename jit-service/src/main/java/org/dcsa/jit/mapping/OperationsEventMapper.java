package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.dcsa.skernel.infrastructure.services.mapping.AddressMapper;
import org.dcsa.skernel.infrastructure.services.mapping.LocationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {
      TransportCallMapper.class,
      PartyMapper.class,
      AddressMapper.class,
      LocationMapper.class,
    })
public interface OperationsEventMapper {
  @Mapping(target = "milesRemainingToDestination", source = "milesToDestinationPort")
  @Mapping(target = "milesToDestinationPort", source = "milesToDestinationPort")
  OperationsEventTO toTO(OperationsEvent operationsEvent);
}
