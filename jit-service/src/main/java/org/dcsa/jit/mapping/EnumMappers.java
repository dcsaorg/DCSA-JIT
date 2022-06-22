package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.enums.DCSATransportType;
import org.dcsa.jit.transferobjects.enums.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnumMappers {
  DCSATransportType modeOfTransportToDao(ModeOfTransport mode);

  org.dcsa.jit.persistence.entity.enums.FacilityTypeCodeOPR facilityTypeCodeOPRToDao(
      FacilityTypeCodeOPR code);

  org.dcsa.jit.persistence.entity.enums.FacilityTypeCodeTRN facilityTypeCodeToDao(
      FacilityTypeCodeTRN code);

  org.dcsa.jit.persistence.entity.enums.OperationsEventTypeCode operationsEventTypeCodeFromDao(
      OperationsEventTypeCode code);

  PortCallPhaseTypeCode portCallPhaseTypeCodeCodeFromDao(
      org.dcsa.jit.persistence.entity.enums.PortCallPhaseTypeCode code);

  org.dcsa.skernel.domain.persistence.entity.enums.FacilityCodeListProvider
      facilityCodeListProviderToDao(FacilityCodeListProvider code);

  org.dcsa.jit.persistence.entity.enums.EventClassifierCode eventClassifierCodetoDao(
      EventClassifierCode eventClassifierCode);

  org.dcsa.jit.persistence.entity.enums.PortCallPhaseTypeCode portCallPhaseTypeCodeCodetoDao(
      PortCallPhaseTypeCode portCallServiceTypeCode);

  PortCallServiceTypeCode portCallServiceTypeCodeFromDao(
      org.dcsa.jit.persistence.entity.enums.PortCallServiceTypeCode portCallServiceTypeCode);

  org.dcsa.jit.persistence.entity.enums.PortCallServiceTypeCode portCallServiceTypeCodeToDao(
      PortCallServiceTypeCode portCallServiceTypeCode);

  org.dcsa.jit.persistence.entity.enums.PublisherRole publisherRoleToDao(
      PublisherRole publisherRole);
}
