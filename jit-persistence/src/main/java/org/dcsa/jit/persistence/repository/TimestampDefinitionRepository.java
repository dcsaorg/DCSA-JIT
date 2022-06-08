package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.persistence.entity.enums.*;
import org.dcsa.jit.persistence.entity.enums.PortCallServiceTypeCode;
import org.dcsa.skernel.domain.persistence.entity.enums.FacilityTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

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

  @Query(value="INSERT INTO ops_event_timestamp_definition (event_id, timestamp_definition) VALUES (:eventID, :timestampDefinitionID)", nativeQuery = true)
  void markOperationsEventAsTimestamp(UUID eventID, String timestampDefinitionID);

 // @Modifying
 // @Query("UPDATE ops_event_timestamp_definition SET payload_id = :payloadID WHERE event_id = :eventID")
 // void linkPayload(UUID eventID, UUID payloadID);
}

