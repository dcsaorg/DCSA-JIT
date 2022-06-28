package org.dcsa.jit.persistence.entity.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PublisherRole {
  CA("Carrier operations"),
  AG("Carrier local agent"),
  VSL("Vessel"),
  ATH("Port Authorities"),
  PLT("Pilot"),
  TWG("Towage provider"),
  LSH("Lashing provider"),
  BUK("Bunkering service provider"),
  TR("Terminal"),
  SLU("Sludge service provider"),
  SVP("Other service provider"),
  MOR("Moording service provider")
  ;

  @Getter
  private final String name;
}
