package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.TransportCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransportCallRepository extends JpaRepository<TransportCall, UUID> {
  @Query(value = "SELECT transport_call.* FROM transport_call" +
    " JOIN mode_of_transport ON (mode_of_transport.mode_of_transport_code = transport_call.mode_of_transport_code)" +
    " JOIN voyage import_voyage ON (transport_call.import_voyage_id = import_voyage.id)" +
    " JOIN voyage export_voyage ON (transport_call.export_voyage_id = export_voyage.id)" +
    " JOIN service ON (service.id = export_voyage.service_id)" +
    " JOIN vessel ON (vessel.id = transport_call.vessel_id)" +
    " JOIN location ON (location.id = transport_call.location_id)" +
    " LEFT JOIN facility ON (location.facility_id = facility.id)" +
    " WHERE vessel.vessel_imo_number = :vesselIMONumber" +
    "   AND mode_of_transport.dcsa_transport_type = :modeOfTransport" +
    "   AND location.un_location_code = :UNLocationCode" +
    "   AND ((:facilitySMDGCode1 IS NULL AND facility.id IS NULL) OR (facility.facility_smdg_code = :facilitySMDGCode2))" +
    "   AND import_voyage.carrier_voyage_number = :importVoyageNumber" +
    "   AND export_voyage.carrier_voyage_number = :exportVoyageNumber" +
    "   AND service.carrier_service_code = :carrierServiceCode" +
    "   AND (:transportCallSequenceNumber IS NULL OR transport_call.transport_call_sequence_number = :transportCallSequenceNumber)" +
    " LIMIT 2", nativeQuery = true)
  List<TransportCall> findAllTransportCall(
    @Param("UNLocationCode") String UNLocationCode,
    @Param("facilitySMDGCode1") String facilitySMDGCode1,
    @Param("facilitySMDGCode2") String facilitySMDGCode2, // facilitySMDGCode1 and facilitySMDGCode2 should be the same - this is a workaround for JPA native queries
    @Param("modeOfTransport") String modeOfTransport,
    @Param("vesselIMONumber") String vesselIMONumber,
    @Param("carrierServiceCode") String carrierServiceCode,
    @Param("importVoyageNumber") String importVoyageNumber,
    @Param("exportVoyageNumber") String exportVoyageNumber,
    @Param("transportCallSequenceNumber") Integer transportCallSequenceNumber
  );
}
