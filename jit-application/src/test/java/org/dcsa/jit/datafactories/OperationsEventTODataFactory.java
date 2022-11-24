package org.dcsa.jit.datafactories;

import lombok.experimental.UtilityClass;
import org.dcsa.jit.transferobjects.*;
import org.dcsa.jit.transferobjects.enums.*;
import org.dcsa.skernel.infrastructure.transferobject.LocationTO;
import org.dcsa.skernel.infrastructure.transferobject.enums.FacilityCodeListProvider;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class OperationsEventTODataFactory {

  public List<OperationsEventTO> operationsEventTOList() {

    PartyTO partyTO = PartyTO.builder().partyName("FDM Quality Control").build();

    LocationTO locationTO =
        LocationTO.UNLocationLocationTO.builder()
            .locationName("Copenhagen")
            .UNLocationCode("DKCPH")
            .build();

    TransportCallVesselTO vesselTO =
        TransportCallVesselTO.builder()
            .vesselIMONumber("9321483")
            .vesselName("Emma Maersk")
            .vesselFlag("DK")
            .vesselOperatorCarrierCode("MSK")
            .vesselOperatorCarrierCodeListProvider(CarrierCodeListProvider.SMDG)
            .build();

    TransportCallTO transportCallTO =
        TransportCallTO.builder()
            .transportCallReference("TC-REF-08_01-A")
            .exportVoyageNumber("2107E")
            .importVoyageNumber("2106W")
            .carrierExportVoyageNumber("2107E")
            .carrierImportVoyageNumber("2106W")
            .transportCallSequenceNumber(1)
            .facilityCode("PSABT")
            .facilityCodeListProvider(FacilityCodeListProvider.SMDG)
            .facilityTypeCode(FacilityTypeCodeTRN.POTE)
            .modeOfTransport(ModeOfTransport.VESSEL)
            .vessel(vesselTO)
            .build();

    LocationTO vesselPosition =
        LocationTO.GeoLocationTO.builder()
            .locationName("Orlando")
            .latitude("28.481° N")
            .longitude("-81.48° E")
            .build();

    OperationsEventTO operationsEventTO =
        OperationsEventTO.builder()
            .eventID(UUID.fromString("d330b6f5-edcb-4e9e-a09f-e98e91deba95"))
            .eventType("OPERATIONS")
            .eventClassifierCode(EventClassifierCode.REQ)
            .operationsEventTypeCode(OperationsEventTypeCode.ARRI)
            .publisher(partyTO)
            .publisherRole(PublisherRole.TR)
            .facilityTypeCode(FacilityTypeCodeOPR.BRTH)
            .eventLocation(locationTO)
            .portCallPhaseTypeCode(PortCallPhaseTypeCode.INBD)
            .transportCall(transportCallTO)
            .vesselPosition(vesselPosition)
            .vesselDraftUnit(DimensionUnit.FOT)
            .milesRemainingToDestination(3.0f)
            .milesToDestinationPort(3.0f)
            .build();

    return Collections.singletonList(operationsEventTO);
  }
}
