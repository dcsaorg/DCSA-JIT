package org.dcsa.jit.itests.config;

import io.restassured.RestAssured;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RestAssuredConfigurator {

  // API endpoints
  public static final String EVENTS = "/jit/v1/events";
  public static final String TIMESTAMPS = "/jit/v1/timestamps";

  public void initialize() {
    var properties = IntegrationTestsProperties.getInstance();
    RestAssured.baseURI = properties.getBaseUri();
    RestAssured.port = properties.getPort();
  }
}
