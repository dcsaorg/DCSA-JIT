package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;
import org.dcsa.jit.persistence.entity.enums.OperationsEventTypeCode;
import org.dcsa.jit.persistence.entity.enums.PortCallPhaseTypeCode;
import org.dcsa.jit.persistence.entity.enums.PortCallServiceTypeCode;
import org.dcsa.skernel.domain.persistence.entity.enums.FacilityTypeCode;
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
          FacilityTypeCode facilityTypeCode);
}

