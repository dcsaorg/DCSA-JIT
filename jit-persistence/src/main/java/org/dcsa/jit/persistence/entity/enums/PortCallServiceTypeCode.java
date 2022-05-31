package org.dcsa.jit.persistence.entity.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PortCallServiceTypeCode {
  PILO("Pilotage"),
  MOOR("Mooring"),
  CRGO("Cargo operations"),
  TOWG("Towage"),
  BUNK("Bunkering"),
  WSDP("Waste disposal"),
  LASH("Lashing"),
  SAFE("Safety"),
  FAST("Fast"),
  GWAY("Gangway")
  ;

  @Getter
  private final String name;
}
