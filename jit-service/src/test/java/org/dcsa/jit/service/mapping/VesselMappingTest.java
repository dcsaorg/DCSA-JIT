package org.dcsa.jit.service.mapping;

import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.transferobjects.VesselTO;
import org.dcsa.skernel.test.helpers.FieldValidator;
import org.junit.jupiter.api.Test;

public class VesselMappingTest {
  @Test
  public void testTargetFieldsPresentInSrc() {
    FieldValidator.assertTargetFieldsPresentInSrc(Vessel.class, VesselTO.class,
      // special mappings
      "vesselDraft",
      "vesselCallSignNumber",
      "vesselFlag",
      "vesselName",
      // Unmapped
      "vesselOperatorCarrierCodeListProvider", "vesselOperatorCarrierCode");
  }
}
