package org.dcsa.jit.persistence.entity.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum VesselTypeCode {
  GCGO("General cargo"),
  CONT("Container"),
  RORO("RoRo"),
  CARC("Car carrier"),
  PASS("Passenger"),
  FERY("Ferry"),
  BULK("Bulk"),
  TANK("Tanker"),
  LGTK("Liquified gaz tanker"),
  ASSI("Assistance"),
  PILO("Pilot boat");

  @Getter private final String description;
}
