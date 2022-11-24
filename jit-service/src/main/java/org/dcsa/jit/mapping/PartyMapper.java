package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.Party;
import org.dcsa.jit.transferobjects.PartyTO;
import org.dcsa.skernel.infrastructure.services.mapping.AddressMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface PartyMapper {
  Party toDao(PartyTO partyTo);
  PartyTO toTO(Party party);
}
