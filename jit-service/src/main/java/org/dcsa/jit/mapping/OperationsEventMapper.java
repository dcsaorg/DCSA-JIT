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
  @Mapping(target = "eventCreatedDateTime", source = "createdDateTime")
  @Mapping(target = "eventClassifierCode", source = "classifierCode")
  @Mapping(target = "eventDateTime", source = "dateTime")
  OperationsEventTO toTO(OperationsEvent operationsEvent);
}
