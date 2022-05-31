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
  TR("Terminal")
  ;

  @Getter
  private final String name;
}
