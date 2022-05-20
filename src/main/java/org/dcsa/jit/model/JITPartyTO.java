package org.dcsa.jit.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dcsa.skernel.model.Address;
import org.dcsa.skernel.model.base.AbstractParty;
import org.dcsa.skernel.model.enums.DCSAResponsibleAgencyCode;
import org.dcsa.skernel.model.transferobjects.PartyTO;

import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class JITPartyTO extends AbstractParty {

  private String nmftaCode;

  private Address address;

  private List<PartyTO.IdentifyingCode> identifyingCodes;

  public void adjustIdentifyingCodesIfNmftaIsPresent(){
    if (StringUtils.isNotEmpty(this.getNmftaCode())) {
      if (null != identifyingCodes
        && !identifyingCodes.isEmpty()
        && identifyingCodes.stream()
        .anyMatch(
          idc ->
            DCSAResponsibleAgencyCode.SCAC
              .getLegacyAgencyCode()
              .equals(idc.getCodeListResponsibleAgencyCode()))) {

        for (PartyTO.IdentifyingCode idc : this.identifyingCodes) {
          if(DCSAResponsibleAgencyCode.SCAC
            .getLegacyAgencyCode()
            .equals(idc.getCodeListResponsibleAgencyCode())){
            idc.setPartyCode(this.getNmftaCode());
          }
        }

      } else if (null == identifyingCodes || identifyingCodes.isEmpty()) {
        this.identifyingCodes =
          Collections.singletonList(
            PartyTO.IdentifyingCode.builder()
              .codeListResponsibleAgencyCode(DCSAResponsibleAgencyCode.SCAC.getLegacyAgencyCode())
              .partyCode(this.getNmftaCode())
              .build());
      } else {
        identifyingCodes.add(
          PartyTO.IdentifyingCode.builder()
            .codeListResponsibleAgencyCode(DCSAResponsibleAgencyCode.SCAC.getLegacyAgencyCode())
            .partyCode(this.getNmftaCode())
            .build());
      }
    }
  }
}
