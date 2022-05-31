package org.dcsa.jit.persistence.entity.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum FacilityTypeCode {
  BOCR("Border crossing"),
  CLOC("Customer location"),
  COFS("Container freight station"),
  @Deprecated() COYA("Container yard"), // now called OFFD
  OFFD("Off dock storage"),
  DEPO("Depot"),
  INTE("Inland terminal"),
  POTE("Port terminal"),
  PBPL("Pilot boarding place"),
  BRTH("Berth"),
  RAMP("Ramp")
  ;

  @Getter
  private final String name;

  public final static String OperationsEventFacilityTypeCodes = "PBPL,BRTH";
}
