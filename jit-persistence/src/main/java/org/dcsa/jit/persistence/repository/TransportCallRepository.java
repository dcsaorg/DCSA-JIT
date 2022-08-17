package org.dcsa.jit.persistence.repository;

import org.dcsa.jit.persistence.entity.TransportCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransportCallRepository extends JpaRepository<TransportCall, UUID> {

  // The "null AS port_visit_id" is because JPA *insists* on doing OneToOne's eagerly and assumes the column is present
  @Query(value = """
     SELECT transport_call.*, null AS port_visit_id FROM transport_call
       JOIN mode_of_transport ON (mode_of_transport.mode_of_transport_code = transport_call.mode_of_transport_code)
       LEFT JOIN voyage import_voyage ON (transport_call.import_voyage_id = import_voyage.id)
       LEFT JOIN voyage export_voyage ON (transport_call.export_voyage_id = export_voyage.id)
       LEFT JOIN service ON (service.id = export_voyage.service_id)
       JOIN vessel ON (vessel.id = transport_call.vessel_id)
       JOIN location ON (location.id = transport_call.location_id)
       LEFT JOIN facility ON (location.facility_id = facility.id)
       WHERE vessel.vessel_imo_number = :vesselIMONumber
         AND mode_of_transport.dcsa_transport_type = :modeOfTransport
         AND location.un_location_code = :UNLocationCode
         AND ((:facilitySMDGCode IS NULL AND facility.id IS NULL) OR (facility.facility_smdg_code = :facilitySMDGCode))
         AND (:carrierImportVoyageNumber IS NULL OR import_voyage.carrier_voyage_number = :carrierImportVoyageNumber)
         AND (:carrierExportVoyageNumber IS NULL OR export_voyage.carrier_voyage_number = :carrierExportVoyageNumber)
         AND service.carrier_service_code = :carrierServiceCode
         AND (:transportCallSequenceNumber IS NULL OR transport_call.transport_call_sequence_number = :transportCallSequenceNumber)
         AND (:portVisitReference IS NULL OR transport_call.port_visit_reference = :portVisitReference)
       LIMIT 2
     """, nativeQuery = true)
  List<TransportCall> findAllTransportCall(
    @Param("UNLocationCode") String UNLocationCode,
    @Param("facilitySMDGCode") String facilitySMDGCode,
    @Param("modeOfTransport") String modeOfTransport,
    @Param("vesselIMONumber") String vesselIMONumber,
    @Param("carrierServiceCode") String carrierServiceCode,
    @Param("carrierImportVoyageNumber") String carrierImportVoyageNumber,
    @Param("carrierExportVoyageNumber") String carrierExportVoyageNumber,
    @Param("transportCallSequenceNumber") Integer transportCallSequenceNumber,
    @Param("portVisitReference") String portVisitReference
  );

  @Modifying
  @Query(value = "INSERT INTO transport_call_jit_port_visit (port_visit_id, transport_call_id) VALUES (:portVisitID, :transportCallID)", nativeQuery = true)
  void linkPortVisitWithTransportCall(UUID portVisitID, UUID transportCallID);
}
