package org.dcsa.jit.model.mapper;


import org.dcsa.jit.model.JITPartyTO;
import org.dcsa.skernel.model.transferobjects.PartyTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JITPartyTOMapper {

  PartyTO jitPartyTOtoPartyTO(JITPartyTO partyTO);
}
