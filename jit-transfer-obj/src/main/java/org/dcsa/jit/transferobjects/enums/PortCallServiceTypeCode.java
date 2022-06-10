package org.dcsa.jit.transferobjects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

import static org.dcsa.jit.transferobjects.enums.EventClassifierCode.*;
import static org.dcsa.jit.transferobjects.enums.FacilityTypeCode.BRTH;
import static org.dcsa.jit.transferobjects.enums.PortCallPhaseTypeCode.*;

@AllArgsConstructor
@Getter
public enum PortCallServiceTypeCode {
  PILO("Pilotage",null,EnumSet.of(REQ, PLN, ACT), EnumSet.of(INBD, OUTB), "Pilotage"),
  /* Timestamp partyName not confirmed (not mentioned in JIT 1.1) */
  MOOR("Mooring",null,Set.of(), Set.of(), "Mooring"),
  CRGO("Cargo operations",BRTH, EnumSet.allOf(EventClassifierCode.class), EnumSet.of(INBD, ALGS), "Cargo Ops"),
  TOWG("Towage",null, EnumSet.of(REQ, PLN, ACT), EnumSet.of(INBD, OUTB), "Towage"),
  BUNK("Bunkering",BRTH, EnumSet.allOf(EventClassifierCode.class), EnumSet.of(INBD, ALGS), "Bunkering"),
  WSDP("Waste disposal",null, Set.of(), Set.of(), "Waste disposal"),
  LASH("Lashing",BRTH, EnumSet.of(ACT), EnumSet.of(ALGS), "Lashing"),
  SAFE("Safety",null, EnumSet.of(ACT), EnumSet.of(ALGS), null),
  FAST("All Fast",null, EnumSet.of(ACT), EnumSet.of(ALGS), "AT All Fast"),
  GWAY("Gangway down and secure",null, EnumSet.of(ACT), EnumSet.of(ALGS), "Gangway Down and Safe"),
  ;

  private final String name;
  @Getter
  private final FacilityTypeCode expectedFacilityTypeCode;
  @Getter
  private final Set<EventClassifierCode> validEventClassifiers;
  @Getter
  private final Set<PortCallPhaseTypeCode> validPhases;
  @Getter
  private final String timestampTypeBaseName;


  public boolean isValidEventClassifierCode(EventClassifierCode eventClassifierCode) {
    return this.validEventClassifiers.contains(eventClassifierCode);
  }

  public boolean isValidPhase(PortCallPhaseTypeCode portCallPhaseTypeCode) {
    if (portCallPhaseTypeCode == null) {
      // Backwards compat: Accept null in place of a single valid phase
      return validPhases.isEmpty() || validPhases.size() == 1;
    }
    return validPhases.contains(portCallPhaseTypeCode);
  }
}
