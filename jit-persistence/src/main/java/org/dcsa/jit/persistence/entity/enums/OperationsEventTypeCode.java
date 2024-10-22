package org.dcsa.jit.persistence.entity.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum OperationsEventTypeCode {
  ARRI("Arrived"),
  DEPA("Departed"),
  STRT("Started"),
  CMPL("Completed"),

  CANC("Cancelled"),

  OMIT("Omitted")
  ;

  @Getter
  private final String name;
}
