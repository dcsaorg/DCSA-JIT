package org.dcsa.jit.transferobjects;

import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.FacilityTypeCodeTRN;
import org.dcsa.jit.transferobjects.enums.ModeOfTransport;
import org.dcsa.skernel.infrastructure.transferobject.LocationTO;
import org.dcsa.skernel.infrastructure.transferobject.enums.FacilityCodeListProvider;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TransportCallTO(
  @NotNull @Size(max = 100) String transportCallReference,
  @Size(max = 5) String carrierServiceCode,
  @Deprecated @Size(max = 50) String exportVoyageNumber, // Deprecated in JIT 1.2
  @Deprecated @Size(max = 50) String importVoyageNumber, // Deprecated in JIT 1.2
  @Size(max = 50) String carrierExportVoyageNumber,
  @Size(max = 50) String carrierImportVoyageNumber,
  Integer transportCallSequenceNumber,
  @Deprecated @Size(max = 5) String UNLocationCode, // Deprecated in JIT 1.2
  @Deprecated @Size(max = 6) String facilityCode, // Deprecated in JIT 1.2
  @Deprecated FacilityCodeListProvider facilityCodeListProvider, // Deprecated in JIT 1.2
  @Deprecated FacilityTypeCodeTRN facilityTypeCode, // Deprecated in JIT 1.2
  @Deprecated @Size(max = 50) String otherFacility, // Deprecated in JIT 1.2
  @NotNull ModeOfTransport modeOfTransport,
  LocationTO location,
  TransportCallVesselTO vessel,
  @Size(max = 50) String portVisitReference
) {
  @Builder(toBuilder = true) // workaround for intellij issue
  public TransportCallTO {}
}
