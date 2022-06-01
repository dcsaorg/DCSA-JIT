package org.dcsa.jit.transferobjects;

import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.DCSAResponsibleAgencyCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record IdentifyingCodeTO(
  @NotNull DCSAResponsibleAgencyCode DCSAResponsibleAgencyCode,
  @NotNull @Size(max = 100) String partyCode,
  @Size(max = 100) String codeListName
) {
  @Builder // workaround for intellij issue
  public IdentifyingCodeTO { }
}
