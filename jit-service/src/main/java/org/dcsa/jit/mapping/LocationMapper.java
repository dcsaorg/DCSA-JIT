package org.dcsa.jit.mapping;

import org.dcsa.jit.transferobjects.LocationTO;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {
  Location toDao(LocationTO locationTO);
}
