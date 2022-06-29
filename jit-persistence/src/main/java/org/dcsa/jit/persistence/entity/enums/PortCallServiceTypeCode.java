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
  LASH("Lashing"),
  SAFE("Safety"),
  FAST("Fast"),
  GWAY("Gangway"),
  ANCO ("Anchorage operations"),
  SLUG ("Sludge"),
  SHPW ("Shore Power"),
  LCRO ("Loading cargo operations"),
  DCRO ("Discharge cargo operations"),
  VRDY("Vessel ready");

  @Getter
  private final String name;
}
