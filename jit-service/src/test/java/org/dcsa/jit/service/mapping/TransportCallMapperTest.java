package org.dcsa.jit.service.mapping;

import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.transferobjects.TransportCallTO;
import org.dcsa.skernel.test.helpers.FieldValidator;
import org.junit.jupiter.api.Test;

public class TransportCallMapperTest {
  @Test
  public void test() {
    FieldValidator.assertFieldsAreEqual(TransportCall.class, TransportCallTO.class,
      // Special mappings
      "importVoyageNumber", "exportVoyageNumber", "UNLocationCode", "carrierServiceCode",
      "modeOfTransport", "modeOfTransportCode", "importVoyage", "exportVoyage", "id", "facility",
      "facilityCode", "facilityCodeListProvider",
      // Currently not mapped
      "otherFacility", "portCallStatusCode"
      );
  }
}
