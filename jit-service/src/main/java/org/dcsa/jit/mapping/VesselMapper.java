package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.transferobjects.VesselTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VesselMapper {
  @Mapping(target = "vesselIMONumber", source = "imoNumber")
  @Mapping(target = "vesselName", source = "name")
  @Mapping(target = "vesselFlag", source = "flag")
  @Mapping(target = "vesselCallSignNumber", source = "callSign")
  VesselTO toTo(Vessel vessel);
}
