package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.transferobjects.VesselTO;
import org.dcsa.jit.transferobjects.enums.CarrierCodeListProvider;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VesselMapper {
  VesselTO toTO(Vessel vessel);

  @Mapping(target = "isDummy", source = "isDummy", defaultValue = "false")
  Vessel toEntity(VesselTO vesselTO);

  @AfterMapping
  default void mapVesselOperatorCarrier(
      Vessel vessel, @MappingTarget VesselTO.VesselTOBuilder vesselTOBuilder) {
    String nMFTACode = vessel.getVesselOperatorCarrier().getNmftaCode();
    String sMDGCode = vessel.getVesselOperatorCarrier().getSmdgCode();
    if (nMFTACode != null) {
      vesselTOBuilder.vesselOperatorCarrierCodeListProvider(CarrierCodeListProvider.NMFTA);
      vesselTOBuilder.vesselOperatorCarrierCode(vessel.getVesselOperatorCarrier().getNmftaCode());
    } else if (sMDGCode != null) {
      vesselTOBuilder.vesselOperatorCarrierCodeListProvider(CarrierCodeListProvider.SMDG);
      vesselTOBuilder.vesselOperatorCarrierCode(vessel.getVesselOperatorCarrier().getSmdgCode());
    }
  }
}
