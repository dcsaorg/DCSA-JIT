package org.dcsa.jit.service.mapping;

import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.dcsa.skernel.test.helpers.FieldValidator;
import org.junit.jupiter.api.Test;

public class OperationsEventMapperTest {
  @Test
  public void testFieldsAreEqual() {
    FieldValidator.assertFieldsAreEqual(
        OperationsEvent.class,
        OperationsEventTO.class,
        // Unmapped fields
        "milesToDestinationPort",
        "eventType",
        "facilityTypeCode");
  }
}
