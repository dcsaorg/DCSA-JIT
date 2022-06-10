package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.transferobjects.TransportCallTO;
import org.mapstruct.EnumMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring",
    uses = {VesselMapper.class, LocationMapper.class})
public interface TransportCallMapper {
  @Mappings(
      value = {
        @Mapping(target = "transportCallReference", source = "reference"),
        @Mapping(target = "importVoyageNumber", source = "importVoyage.carrierVoyageNumber"),
        @Mapping(target = "exportVoyageNumber", source = "exportVoyage.carrierVoyageNumber"),
        @Mapping(target = "transportCallSequenceNumber", source = "sequenceNumber"),
        @Mapping(target = "unLocationCode", source = "location.unLocationCode"),
      })
  TransportCallTO toTO(TransportCall transportCall);
}
