package org.dcsa.jit.enums;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@AllArgsConstructor
@Getter
public enum PublisherRole {
  CA("Carrier"),
  AG("Carrier local agent"),
  VSL("Experimental: Vessel"),
  ATH("Experimental: Port Authorities"),
  PLT("Experimental: Port Pilot"),
  TR("Terminal"),
  TWG("Experimental: Towage serice provider"),
  LSH("Experimental: Lashing serice provider"),
  BUK("Experimental: Bunker serice provider");

  private final String value;
}
