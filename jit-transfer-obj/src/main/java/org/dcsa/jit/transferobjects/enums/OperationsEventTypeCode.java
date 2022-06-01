package org.dcsa.jit.transferobjects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OperationsEventTypeCode {
  STRT("Started"),
  CMPL("Completed"),
  ARRI("Arrived"),
  DEPA("Departed");
  private final String value;
}
