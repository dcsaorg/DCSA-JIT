package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.transferobjects.TimestampVesselTO;
import org.dcsa.jit.transferobjects.VesselTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface VesselMapper {
  // FIXME @Mapping(target = "vesselOperatorCarrierCode", source = "vesselOperatorCarrier.") // smdgCode or nmftaCode
  VesselTO toTO(Vessel vessel);

  @Mapping(target = "isDummy", source = "isDummy", defaultValue = "false")
  Vessel toEntity(VesselTO vesselTO);

  @Mappings(value = {
    @Mapping(target = "vesselName", source = "name"),
    @Mapping(target = "length", source = "lengthOverall"),
    @Mapping(target = "width", source = "width"),
    @Mapping(target = "vesselCallSignNumber", source = "callSign"),
    @Mapping(target = "isDummy", constant = "false")
  })
  Vessel toEntity(TimestampVesselTO vesselTO);
}
