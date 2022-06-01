package org.dcsa.jit.transferobjects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DCSAResponsibleAgencyCode {
  ISO (""),
  UNECE (""),
  LLOYD ("Lloyd's register of shipping"),
  BIC ("Bureau International des Containeurs"),
  IMO (""),
  SCAC ("Standard Carrier Alpha Code"),
  ITIGG (""),
  ITU (""),
  SMDG (""),
  EXIS (""),
  FMC ("Federal Maritime Commission"),
  CBSA ("Canada Border Services Agency"),
  ZZZ ("Mutually defined");
  private final String value;
}
