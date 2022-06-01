package org.dcsa.jit.transferobjects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventClassifierCode {
  ACT("Actual"),
  PLN("Planned"),
  EST("Estimated"),
  REQ("Requested");
  private final String value;
}
