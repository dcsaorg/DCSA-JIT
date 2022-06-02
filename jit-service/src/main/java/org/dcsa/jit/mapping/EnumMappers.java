package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.enums.DCSATransportType;
import org.dcsa.jit.transferobjects.enums.FacilityTypeCode;
import org.dcsa.jit.transferobjects.enums.ModeOfTransport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnumMappers {
  DCSATransportType modeOfTransportToDao(ModeOfTransport mode);

  org.dcsa.skernel.domain.persistence.entity.enums.FacilityTypeCode facilityTypeCodeToDao(FacilityTypeCode code);
}
