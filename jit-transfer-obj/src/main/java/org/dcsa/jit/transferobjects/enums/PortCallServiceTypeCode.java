package org.dcsa.jit.transferobjects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PortCallServiceTypeCode {
  PILO("Pilotage"),
  MOOR("Mooring"),
  CRGO("Cargo operations"),
  TOWG("Towage"),
  BUNK("Bunkering"),
  WSDP("Waste disposal"),
  LASH("Lashing"),
  SAFE("Safety"),
  FAST("All Fast"),
  GWAY("Gangway down and secure");
  private final String value;
}
