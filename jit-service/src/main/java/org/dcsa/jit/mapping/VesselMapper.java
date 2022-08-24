package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.transferobjects.TransportCallVesselTO;
import org.dcsa.jit.transferobjects.UISupportVesselTO;
import org.dcsa.jit.transferobjects.enums.CarrierCodeListProvider;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface VesselMapper {
  @Mappings(value = {
    @Mapping(target = "vesselName", source = "name"),
    @Mapping(target = "vesselFlag", source = "flag"),
    @Mapping(target = "vesselCallSignNumber", source = "callSignNumber"),
  })
  TransportCallVesselTO toTO(Vessel vessel);

  @Mappings(value = {
    @Mapping(target = "name", source = "vesselName"),
    @Mapping(target = "flag", source = "vesselFlag"),
    @Mapping(target = "callSignNumber", source = "vesselCallSignNumber"),
    @Mapping(target = "isDummy", constant = "false")
  })
  Vessel toEntity(TransportCallVesselTO transportCallVesselTO);

  @Mappings(value = {
    @Mapping(target = "name", source = "vesselName"),
    @Mapping(target = "flag", source = "vesselFlag"),
    @Mapping(target = "callSignNumber", source = "vesselCallSignNumber"),
    @Mapping(target = "isDummy", constant = "false")
  })
  Vessel toEntity(UISupportVesselTO vesselTO);

  @Mappings(value = {
    @Mapping(target = "vesselName", source = "name"),
    @Mapping(target = "vesselFlag", source = "flag"),
    @Mapping(target = "vesselCallSignNumber", source = "callSignNumber")
  })
  UISupportVesselTO toUISupportVesselTO(Vessel vessel);

  @AfterMapping
  default void mapVesselOperatorCarrier(
      Vessel vessel, @MappingTarget TransportCallVesselTO.TransportCallVesselTOBuilder vesselTOBuilder) {
    if (vessel.getVesselOperatorCarrier() == null) {
      return;
    }
    String nMFTACode = vessel.getVesselOperatorCarrier().getNmftaCode();
    String sMDGCode = vessel.getVesselOperatorCarrier().getSmdgCode();
    if (sMDGCode != null) {
      vesselTOBuilder.vesselOperatorCarrierCodeListProvider(CarrierCodeListProvider.SMDG);
      vesselTOBuilder.vesselOperatorCarrierCode(sMDGCode);
    } else if (nMFTACode != null) {
      vesselTOBuilder.vesselOperatorCarrierCodeListProvider(CarrierCodeListProvider.NMFTA);
      vesselTOBuilder.vesselOperatorCarrierCode(nMFTACode);
    }
  }

  @AfterMapping
  default void mapVesselOperatorCarrier(
    Vessel vessel, @MappingTarget UISupportVesselTO.UISupportVesselTOBuilder vesselTOBuilder) {
    if (vessel.getVesselOperatorCarrier() == null) {
      return;
    }
    String nMFTACode = vessel.getVesselOperatorCarrier().getNmftaCode();
    String sMDGCode = vessel.getVesselOperatorCarrier().getSmdgCode();
    if (sMDGCode != null) {
      vesselTOBuilder.vesselOperatorCarrierCodeListProvider(CarrierCodeListProvider.SMDG);
      vesselTOBuilder.vesselOperatorCarrierCode(sMDGCode);
    } else if (nMFTACode != null) {
      vesselTOBuilder.vesselOperatorCarrierCodeListProvider(CarrierCodeListProvider.NMFTA);
      vesselTOBuilder.vesselOperatorCarrierCode(nMFTACode);
    }
  }
}
