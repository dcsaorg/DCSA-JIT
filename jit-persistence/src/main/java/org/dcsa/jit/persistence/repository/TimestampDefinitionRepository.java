package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.persistence.entity.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimestampDefinitionRepository
    extends JpaRepository<TimestampDefinition, String> {

  List<TimestampDefinition>
      findByEventClassifierCodeAndOperationsEventTypeCodeAndPortCallPhaseTypeCodeAndPortCallServiceTypeCodeAndFacilityTypeCode(
          EventClassifierCode eventClassifierCode,
          OperationsEventTypeCode operationsEventTypeCode,
          PortCallPhaseTypeCode portCallPhaseTypeCode,
          PortCallServiceTypeCode portCallServiceTypeCode,
          FacilityTypeCodeOPR facilityTypeCode);

  List<TimestampDefinition>
      findByEventClassifierCodeAndOperationsEventTypeCodeAndProvidedInStandardAndPortCallServiceTypeCodeAndFacilityTypeCode(
          EventClassifierCode eventClassifierCode,
          OperationsEventTypeCode operationsEventTypeCode,
          String providedInStandard,
          PortCallServiceTypeCode portCallServiceTypeCode,
          FacilityTypeCodeOPR facilityTypeCode);
}

