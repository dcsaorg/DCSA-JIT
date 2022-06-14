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
  public void testOperationsEventWithoutQueryParameters() {
    given()
        .contentType(ContentType.JSON)
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("size()", greaterThan(0))
        .body("eventCreatedDateTime", everyItem(notNullValue()))
        .body("eventType", everyItem(equalTo("OPERATIONS")))
        .body("eventClassifierCode", everyItem(notNullValue()))
        .body("eventDateTime", everyItem(notNullValue()))
        .body("operationsEventTypeCode", everyItem(notNullValue()))
        .body("publisher", everyItem(notNullValue()))
        .body("publisherRole", everyItem(notNullValue()));
  }

  @Test
  public void testOperationsEventWithLimit1() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("limit", 1)
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("size()", equalTo(1));
  }

  @Test
  public void testOperationsEventWithLimit2() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("limit", 2)
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("size()", equalTo(2));
  }

  @Test
  public void testOperationsEventWithTransportCallIdQueryParameter() {
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
        .body("transportCall", notNullValue())
        .body("transportCall.transportCallReference", everyItem(equalTo("TC-REF-08_03-A")))
        .body("transportCall.UNLocationCode", everyItem(equalTo("USNYC")))
        .body("transportCall.location", notNullValue())
        .body("transportCall.vessel.vesselIMONumber", everyItem(equalTo("9811000")));
  }

  @Test
  public void testWithUnLocationCodeQueryParameter1() {
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

  @Test
  public void testWithUnLocationCodeQueryParameter2() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("UNLocationCode", "SGSIN")
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("size()", greaterThan(0))
        .body("transportCall.location.UNLocationCode", everyItem(equalTo("SGSIN")));
  }

  @Test
  public void testWithVeselImoNumberQueryParameter() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("vesselIMONumber", "9811000")
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("size()", greaterThanOrEqualTo(2))
        .body("transportCall.vessel.vesselIMONumber", everyItem(equalTo("9811000")));
  }

  @Test
  public void testWithOperationsEventTypeCodeQueryParameter1() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("operationsEventTypeCode", "ARRI")
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("operationsEventTypeCode", everyItem(equalTo("ARRI")));
  }

  @Test
  public void testWithOperationsEventTypeCodeQueryParameter2() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("operationsEventTypeCode", "DEPA")
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("operationsEventTypeCode", everyItem(equalTo("DEPA")));
  }

  @Test
  public void testWithCarrierVoyageNumberQueryParameter() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("carrierVoyageNumber", "2103S")
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("transportCall.exportVoyageNumber", everyItem(equalTo("2103S")));
  }

  @Test
  public void testWithExportVoyageNumberQueryParameter() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("exportVoyageNumber", "TNT1E")
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("transportCall.exportVoyageNumber", everyItem(equalTo("TNT1E")));
  }

  @Test
  public void testWithCarrierServiceCodeQueryParameter1() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("carrierServiceCode", "TNT1")
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("transportCall.carrierServiceCode", everyItem(equalTo("TNT1")));
  }
  @Test
  public void testWithCarrierServiceCodeQueryParameter2() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("carrierServiceCode", "FE1")
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("transportCall.carrierServiceCode", everyItem(equalTo("FE1")));
  }
}
