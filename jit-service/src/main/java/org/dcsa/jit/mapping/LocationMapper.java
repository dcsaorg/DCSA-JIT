package org.dcsa.jit.mapping;

import org.dcsa.jit.transferobjects.LocationTO;
import org.dcsa.skernel.domain.persistence.entity.Address;
import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {
  Location toDao(LocationTO locationTO);
  @Mapping(
    source = "location.unLocationCode",
    target = "unLocationCode"
  )
  LocationTO locationToDTO(Location location, Address address, Facility facility);
}
