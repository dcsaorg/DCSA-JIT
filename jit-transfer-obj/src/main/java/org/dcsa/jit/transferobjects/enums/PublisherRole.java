package org.dcsa.jit.transferobjects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
  BUK("Experimental: Bunker serice provider"),
  SLU("Sludge service provider"),
  SVP("Other service provider"),
  MOR("Moording service provider");

  private final String value;
}
