package org.dcsa.jit.transferobjects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PortCallServiceTypeCode {
  PILO("Pilotage"),
  /* Timestamp partyName not confirmed (not mentioned in JIT 1.1) */
  MOOR("Mooring"),
  CRGO("Cargo operations"),
  TOWG("Towage"),
  BUNK("Bunkering"),
  LASH("Lashing"),
  SAFE("Safety"),
  FAST("All Fast"),
  GWAY("Gangway down and secure"),
  ANCO("Anchorage operations"),
  SLUG("Sludge"),
  SHPW("Shore Power"),
  LCRO("Loading cargo operations"),
  DCRO("Discharge cargo operations"),
  VRDY("Vessel ready");

  private final String name;
}
