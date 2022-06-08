package org.dcsa.jit.transferobjects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@Getter
public enum DCSAResponsibleAgencyCode {
  ISO ("","5"),
  UNECE ("","6"),
  LLOYD ("Lloyd's register of shipping","11"),
  BIC ("Bureau International des Containeurs","20"),
  IMO ("","54"),
  SCAC ("Standard Carrier Alpha Code","182"),
  ITIGG ("","274"),
  ITU ("","296"),
  SMDG ("","306"),
  EXIS ("","399"),
  FMC ("Federal Maritime Commission",""),
  CBSA ("Canada Border Services Agency",""),
  ZZZ ("Mutually defined", "zzz");
  private final String value;
  private final String legacyAgencyCode;

  private static final Map<String, DCSAResponsibleAgencyCode> LEGACY_CODES_2_DCSA_CODE;


  public String getLegacyAgencyCode() {
    return this.legacyAgencyCode;
  }

  public static DCSAResponsibleAgencyCode legacyCode2DCSACode(String legacyCode) {
    DCSAResponsibleAgencyCode dcsaCode = LEGACY_CODES_2_DCSA_CODE.get(Objects.requireNonNull(legacyCode));
    if (dcsaCode == null) {
      throw new IllegalArgumentException("Invalid code list responsible agency code");
    }
    return dcsaCode;
  }

  public static void ensureIsValidLegacyCode(String legacyCode) {
    if (legacyCode != null) {
      legacyCode2DCSACode(legacyCode);
    }
  }

  static {
    LEGACY_CODES_2_DCSA_CODE = new HashMap<>();
    for (DCSAResponsibleAgencyCode dcsaCode : DCSAResponsibleAgencyCode.values()) {
      if (dcsaCode.legacyAgencyCode.equals("")) {
        continue;
      }
      LEGACY_CODES_2_DCSA_CODE.put(dcsaCode.legacyAgencyCode, dcsaCode);
    }
  }
}

