package org.dcsa.jit.transferobjects;

import lombok.Builder;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record PartyTO(
  @Size(max = 100) String id,
  @Size(max = 100) String name,
  @Size(max = 20) String taxReference1,
  @Size(max = 20) String taxReference2,
  @Size(max = 500) String publicKey,
  AddressTO address,
  List<IdentifyingCodeTO> identifyingCodes
) {
  @Builder // workaround for intellij issue
  public PartyTO { }
}
