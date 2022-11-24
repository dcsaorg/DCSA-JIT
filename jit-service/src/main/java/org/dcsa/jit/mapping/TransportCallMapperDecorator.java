package org.dcsa.jit.mapping;

import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.transferobjects.TransportCallTO;
import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.infrastructure.transferobject.enums.FacilityCodeListProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class TransportCallMapperDecorator extends TransportCallMapper {
  // NOTE constructor injection doesn't work here, mapstruct would generate class with no constructor
  @Autowired
  @Qualifier("delegate")
  private TransportCallMapper delegate;

  @Override
  public TransportCallTO toTO(TransportCall transportCall) {
    Facility facility = null;
    if (transportCall.getLocation() != null) {
      facility = transportCall.getLocation().getFacility();
    }
    if (facility != null) {
      TransportCallTO.TransportCallTOBuilder builder = delegate.toTO(transportCall).toBuilder();
      if (facility.getFacilitySMDGCode() != null) {
        builder.facilityCode(facility.getFacilitySMDGCode());
        builder.facilityCodeListProvider(FacilityCodeListProvider.SMDG);
      } else if (facility.getFacilityBICCode() != null) {
        builder.facilityCode(facility.getFacilityBICCode());
        builder.facilityCodeListProvider(FacilityCodeListProvider.BIC);
      } else {
        throw new IllegalArgumentException("Unsupported facility code list provider.");
      }
      return builder.build();
    }

    return delegate.toTO(transportCall);
  }
}
