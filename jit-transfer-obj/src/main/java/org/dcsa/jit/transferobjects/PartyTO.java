package org.dcsa.jit.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.dcsa.skernel.domain.persistence.entity.Address;
import org.dcsa.skernel.domain.persistence.entity.enums.FacilityCodeListProvider;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.UUID;

public record PartyTO(
  UUID id,
  @Size(max = 100) String name,
  @Size(max = 20) String taxReference1,
  @Size(max = 20) String taxReference2,
  @Size(max = 500) String publicKey,
  Address address
//  List<IdentifyingCode> identifyingCodes;
) {
  @Builder // workaround for intellij issue
  public PartyTO { }
}
