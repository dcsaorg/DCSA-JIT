package org.dcsa.jit.transferobjects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DimensionUnit {
  MTR("Meter"),
  FOT("Foot");

  private final String name;
}
