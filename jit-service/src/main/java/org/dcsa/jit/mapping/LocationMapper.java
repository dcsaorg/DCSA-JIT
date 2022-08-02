package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.transferobjects.LocationTO;
import org.dcsa.jit.transferobjects.VesselTO;
import org.dcsa.jit.transferobjects.enums.CarrierCodeListProvider;
import org.dcsa.jit.transferobjects.enums.FacilityCodeListProvider;
import org.dcsa.skernel.domain.persistence.entity.Address;
import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface LocationMapper {
  Location toDao(LocationTO locationTO);

  LocationTO toTo(Location location);

  @AfterMapping
  default void mapLocationFacility(
    Location location, @MappingTarget LocationTO.LocationTOBuilder locationTOBuilder) {
    if (location.getFacility() == null) {
      return;
    }
    locationTOBuilder.UNLocationCode(location.getFacility().getUNLocationCode());
    String sMDGCode = location.getFacility().getFacilitySMDGCode();
    String bICCode = location.getFacility().getFacilityBICCode();
    if (sMDGCode != null) {
      locationTOBuilder.facilityCodeListProvider(FacilityCodeListProvider.SMDG);
      locationTOBuilder.facilityCode(sMDGCode);
    } else if (bICCode != null) {
      locationTOBuilder.facilityCodeListProvider(FacilityCodeListProvider.BIC);
      locationTOBuilder.facilityCode(bICCode);

    }
  }
}
