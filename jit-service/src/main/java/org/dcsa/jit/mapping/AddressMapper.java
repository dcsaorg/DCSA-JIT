package org.dcsa.jit.mapping;

import org.dcsa.jit.transferobjects.AddressTO;
import org.dcsa.skernel.domain.persistence.entity.Address;

public interface AddressMapper {

  Address toDao(AddressTO addressTO);
  AddressTO toDTO(Address address);
}
