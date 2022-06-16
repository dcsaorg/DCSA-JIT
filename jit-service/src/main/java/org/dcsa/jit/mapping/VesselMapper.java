package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.transferobjects.VesselTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VesselMapper {
  // FIXME @Mapping(target = "vesselOperatorCarrierCode", source = "vesselOperatorCarrier.") // smdgCode or nmftaCode
  VesselTO toTO(Vessel vessel);

  @Mapping(target = "isDummy", source = "isDummy", defaultValue = "false")
  Vessel toEntity(VesselTO vesselTO);
}
