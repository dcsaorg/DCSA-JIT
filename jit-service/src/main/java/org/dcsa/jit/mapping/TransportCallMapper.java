package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.persistence.entity.enums.DCSATransportType;
import org.dcsa.jit.transferobjects.TransportCallTO;
import org.dcsa.jit.transferobjects.enums.ModeOfTransport;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Arrays;

// FIXME LocationMapper isn't being used properly since it requires more than just Location
@Mapper(componentModel = "spring", uses = {VesselMapper.class, LocationMapper.class, EnumMappers.class})
@DecoratedWith(TransportCallMapperDecorator.class)
public abstract class TransportCallMapper {
  @Mappings(
      value = {
        @Mapping(target = "carrierImportVoyageNumber", source = "importVoyage.carrierVoyageNumber"),
        @Mapping(target = "carrierExportVoyageNumber", source = "exportVoyage.carrierVoyageNumber"),
        @Mapping(target = "importVoyageNumber", source = "importVoyage.carrierVoyageNumber"),
        @Mapping(target = "exportVoyageNumber", source = "exportVoyage.carrierVoyageNumber"),
        @Mapping(target = "UNLocationCode", source = "location.UNLocationCode"),
        @Mapping(target = "carrierServiceCode", source = "exportVoyage.service.carrierServiceCode"),
        @Mapping(target = "modeOfTransport", source = "modeOfTransportCode"),
      })
  public abstract TransportCallTO toTO(TransportCall transportCall);

  protected ModeOfTransport modeOfTransportCodetoTO(String modeOfTransportCode) {
    return Arrays.stream(DCSATransportType.values())
      .filter(t -> t.getCode().toString().equals(modeOfTransportCode))
      .findFirst()
      .map(e -> ModeOfTransport.valueOf(e.name()))
      .orElseThrow(() -> ConcreteRequestErrorMessageException.invalidInput(modeOfTransportCode + " is not a valid transport code"));
  }
}
