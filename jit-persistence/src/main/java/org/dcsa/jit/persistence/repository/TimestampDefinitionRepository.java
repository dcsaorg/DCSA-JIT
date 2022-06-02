package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.persistence.entity.enums.*;
import org.dcsa.jit.persistence.entity.enums.PortCallServiceTypeCode;
import org.dcsa.skernel.domain.persistence.entity.enums.FacilityTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TimestampDefinitionRepository
    extends JpaRepository<TimestampDefinition, String> {

  List<TimestampDefinition>
      findByEventClassifierCodeAndOperationsEventTypeCodeAndPortCallPhaseTypeCodeAndPortCallServiceTypeCodeAndFacilityTypeCode(
          String eventClassifierCode,
          OperationsEventTypeCode operationsEventTypeCode,
          PortCallPhaseTypeCode portCallPhaseTypeCode,
          PortCallServiceTypeCode portCallServiceTypeCode,
          FacilityTypeCode facilityTypeCode);

  @Query(value="INSERT INTO ops_event_timestamp_definition (event_id, timestamp_definition) VALUES (:eventID, :timestampDefinitionID)", nativeQuery = true)
  void markOperationsEventAsTimestamp(UUID eventID, String timestampDefinitionID);
}

