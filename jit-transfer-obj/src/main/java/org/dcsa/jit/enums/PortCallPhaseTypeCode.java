package org.dcsa.jit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PortCallPhaseTypeCode {
  INBD("Inbound"),
  ALGS("Alongside"),
  SHIF("Shifting"),
  OUTB("Outbound");
  private final String value;
}
