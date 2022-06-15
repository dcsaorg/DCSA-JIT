package org.dcsa.jit.service.mapping;

import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.jit.transferobjects.LocationTO;
import org.dcsa.skernel.test.helpers.FieldValidator;
import org.junit.jupiter.api.Test;

public class LocationMapperTest {
  @Test
  public void testFieldsAreEqual() {
    FieldValidator.assertFieldsAreEqual(Location.class, LocationTO.class,
      "id",
      // FIXME: unmapped fields
      "facilityCode", "facilityCodeListProvider", "facility"
      );
  }
}
