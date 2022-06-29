package org.dcsa.jit.mapping;

import org.dcsa.jit.transferobjects.AddressTO;
import org.dcsa.skernel.domain.persistence.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

  @Mapping(source = "postCode", target = "postalCode")
  Address toDao(AddressTO addressTO);
  @Mapping(target = "postCode", source = "postalCode")
  AddressTO toDTO(Address address);
}
