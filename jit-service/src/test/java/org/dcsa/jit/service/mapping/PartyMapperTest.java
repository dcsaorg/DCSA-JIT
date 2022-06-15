package org.dcsa.jit.service.mapping;

import org.dcsa.jit.persistence.entity.Party;
import org.dcsa.jit.transferobjects.PartyTO;
import org.dcsa.skernel.test.helpers.FieldValidator;
import org.junit.jupiter.api.Test;

public class PartyMapperTest {
  @Test
  public void testFieldsAreEqual() {
    FieldValidator.assertFieldsAreEqual(Party.class, PartyTO.class,
      "id",
      // Special mappings
      "identifyingCodes",
      // FIXME Unmapped fields
      "nmftaCode"
      );
  }
}
