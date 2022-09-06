package org.dcsa.jit.transferobjects;

import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.DCSAResponsibleAgencyCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record IdentifyingCodeTO(
  @Size(max = 3) String codeListResponsibleAgencyCode,
  @NotNull DCSAResponsibleAgencyCode DCSAResponsibleAgencyCode,
  @NotEmpty @Size(max = 100) String partyCode,
  @Size(max = 100) String codeListName
) {
  @Builder(toBuilder = true) // workaround for intellij issue
  public IdentifyingCodeTO { }



}
