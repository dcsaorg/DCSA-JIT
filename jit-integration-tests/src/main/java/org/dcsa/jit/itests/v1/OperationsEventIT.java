package org.dcsa.jit.itests.v1;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.dcsa.jit.itests.config.RestAssuredConfigurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OperationsEventIT {
  @BeforeAll
  public static void initializeRestAssured() {
    RestAssuredConfigurator.initialize();
  }

  @Test
  public void testWithoutQueryParameters() {
    given()
        .contentType(ContentType.JSON)
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("size()", greaterThan(0));
  }

  @Test
  public void testWithTransportCallIdQueryParameter() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("transportCallID", "TC-REF-08_03-A")
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("size()", equalTo(1))
        .body("publisher", notNullValue())
        .body("transportCall", notNullValue());
  }

  @Test
  public void testWithUnLocationCodeQueryParameter() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("UNLocationCode", "USNYC")
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("size()", greaterThan(0))
        .body("transportCall.location.UNLocationCode", everyItem(equalTo("USNYC")));
  }
}
