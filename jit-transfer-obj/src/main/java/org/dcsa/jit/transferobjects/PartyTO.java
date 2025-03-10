package org.dcsa.jit.transferobjects;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.dcsa.jit.transferobjects.enums.DCSAResponsibleAgencyCode;
import org.dcsa.skernel.infrastructure.transferobject.AddressTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.Collections;
import java.util.List;

public record PartyTO(
  @Size(max = 100) String partyName,
  @Size(max = 20) String taxReference1,
  @Size(max = 20) String taxReference2,
  @Size(max = 500) String publicKey,
  AddressTO address,
  String nmftaCode,
  @Valid List<IdentifyingCodeTO> identifyingCodes
) {
  private static IdentifyingCodeTO idcs;
  @Builder(toBuilder = true) // workaround for intellij issue
  public PartyTO { }
  public void adjustIdentifyingCodesIfNmftaIsPresent(){
    if (StringUtils.isNotEmpty(this.nmftaCode)) {
      if (null != identifyingCodes
        && !identifyingCodes.isEmpty()
        && identifyingCodes.stream()
        .anyMatch(
          idc ->
            DCSAResponsibleAgencyCode.SCAC.getLegacyAgencyCode()
              .equals(idc.DCSAResponsibleAgencyCode().getLegacyAgencyCode()))) {

        for (IdentifyingCodeTO idc : this.identifyingCodes) {
          if(DCSAResponsibleAgencyCode.SCAC
            .getLegacyAgencyCode()
            .equals(idc.DCSAResponsibleAgencyCode().getLegacyAgencyCode())){
            idc.toBuilder().partyCode(this.nmftaCode()).build();
          }
        }

      } else if (null == identifyingCodes || identifyingCodes.isEmpty()) {
        PartyTO.builder().identifyingCodes =
          Collections.singletonList(
            IdentifyingCodeTO.builder()
              .DCSAResponsibleAgencyCode(DCSAResponsibleAgencyCode.SCAC)
              .partyCode(this.nmftaCode())
              .build());
      } else {
        identifyingCodes.add(
          IdentifyingCodeTO.builder()
            .DCSAResponsibleAgencyCode(DCSAResponsibleAgencyCode.SCAC)
            .partyCode(this.nmftaCode())
            .build());
      }
    }
  }

}
