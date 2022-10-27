package org.dcsa.jit.itests.v1;

import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.dcsa.jit.itests.config.RestAssuredConfigurator;
import org.dcsa.jit.transferobjects.enums.EventClassifierCode;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.function.BiConsumer;

import static io.restassured.RestAssured.given;
import static org.dcsa.jit.itests.config.RestAssuredConfigurator.EVENTS;
import static org.dcsa.jit.itests.config.TestUtil.jsonSchemaValidator;
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
        .get("/jit/v1/events")
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
        .body("publisherRole", everyItem(notNullValue()))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  public void testOperationsEventWithLimit1() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("limit", 1)
        .get("/jit/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("size()", equalTo(1))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  public void testOperationsEventWithLimit2() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("limit", 2)
        .get("/jit/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("size()", equalTo(2))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  public void testOperationsEventWithTransportCallIdQueryParameter() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("transportCallID", "TC-REF-08_03-A")
        .get("/jit/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("size()", greaterThanOrEqualTo(1))
        .body("publisher", notNullValue())
        .body("transportCall", notNullValue())
        .body("transportCall.transportCallReference", everyItem(equalTo("TC-REF-08_03-A")))
        .body("transportCall.UNLocationCode", everyItem(equalTo("USNYC")))
        .body("transportCall.location", notNullValue())
        .body("transportCall.vessel.vesselIMONumber", everyItem(equalTo("9811000")))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  public void testWithUnLocationCodeQueryParameter() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("UNLocationCode", "USNYC")
        .get("/jit/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("size()", greaterThan(0))
        .body("transportCall.location.UNLocationCode", everyItem(equalTo("USNYC")))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  public void testWithVeselImoNumberQueryParameter() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("vesselIMONumber", "9811000")
        .get("/jit/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("size()", greaterThanOrEqualTo(2))
        .body("transportCall.vessel.vesselIMONumber", everyItem(equalTo("9811000")))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  public void testWithOperationsEventTypeCodeQueryParameter1() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("operationsEventTypeCode", "ARRI")
        .get("/jit/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("operationsEventTypeCode", everyItem(equalTo("ARRI")))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  public void testWithOperationsEventTypeCodeQueryParameter2() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("operationsEventTypeCode", "DEPA")
        .get("/jit/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("operationsEventTypeCode", everyItem(equalTo("DEPA")))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  public void testWithCarrierVoyageNumberQueryParameter() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("carrierVoyageNumber", "A_carrier_voyage_number")
        .get("/jit/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("transportCall.carrierExportVoyageNumber", everyItem(equalTo("A_carrier_voyage_number")))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  public void testWithExportVoyageNumberQueryParameter() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("carrierExportVoyageNumber", "TNT1E")
        .get("/jit/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("transportCall.carrierExportVoyageNumber", everyItem(equalTo("TNT1E")))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  public void testWithCarrierServiceCodeQueryParameter1() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("carrierServiceCode", "TNT1")
        .get("/jit/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("transportCall.carrierServiceCode", everyItem(equalTo("TNT1")))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  public void testWithCarrierServiceCodeQueryParameter2() {
    given()
        .contentType(ContentType.JSON)
        .queryParam("carrierServiceCode", "FE1")
        .get("/jit/v1/events")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .body("transportCall.carrierServiceCode", everyItem(equalTo("FE1")))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  void testGetAllEventsByExportVoyageNumber() {
    BiConsumer<String, Matcher<String>> runner =
        (s, m) ->
            given()
                .contentType("application/json")
                .queryParam("carrierExportVoyageNumber", s)
                .get(EVENTS)
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(1))
                .body(
                    "eventClassifierCode",
                    everyItem(
                        anyOf(
                            equalTo(EventClassifierCode.EST.toString()),
                            equalTo(EventClassifierCode.ACT.toString()),
                            equalTo(EventClassifierCode.PLN.toString()),
                            equalTo(EventClassifierCode.REQ.toString()))))
                .body("operationsEventTypeCode", everyItem(m))
                .body(jsonSchemaValidator("operationsEvent"));

    runner.accept(
        "A_carrier_voyage_number", anyOf(equalTo("ARRI"), equalTo("DEPA"), (equalTo("STRT"))));
    runner.accept("2107E", equalTo("ARRI"));
  }

  @Test
  void testGetAllEventsByCombinedQuery() {
    given()
        .contentType("application/json")
        .queryParam("carrierExportVoyageNumber", "A_carrier_voyage_number")
        .queryParam("UNLocationCode", "USNYC")
        .get(EVENTS)
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("size()", greaterThanOrEqualTo(1))
        .body("transportCall.carrierExportVoyageNumber", everyItem(equalTo("A_carrier_voyage_number")))
        .body("transportCall.location.UNLocationCode", everyItem(equalTo("USNYC")))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  void testGetAllEventsByTransportCallIDWithEventCreatedDateTimeRange() {
    String rangeStart = "2022-05-08T00:00:00Z";
    String rangeEnd = "2022-05-09T00:00:00Z";
    given()
        .contentType("application/json")
        .queryParam("transportCallID", "TC-REF-08_03-B")
        .queryParam("eventCreatedDateTime:gte", rangeStart)
        .queryParam("eventCreatedDateTime:lt", rangeEnd)
        .get(EVENTS)
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        // validate an exact match.
        .body("size()", equalTo(1))
        .body("eventClassifierCode", everyItem(equalTo(EventClassifierCode.ACT.toString())))
        .body(
            "eventCreatedDateTime",
            everyItem(
                asDateTime(
                    allOf(
                        greaterThanOrEqualTo(ZonedDateTime.parse(rangeStart)),
                        lessThan(ZonedDateTime.parse(rangeEnd))))))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  void testGetAllEventsByEventCreatedDateTimeRange() {
    String rangeStart = "2022-02-08T00:00:00Z";
    // 10:00-0400 is 14:00 at Z, so the first event for CBR 832deb4bd4ea4b728430b857c59bd057 is
    // included while the
    // latter to are excluded
    String rangeEnd = "2022-05-09T10:00:00-04:00";
    given()
        .contentType("application/json")
        .queryParam("eventCreatedDateTime:gte", rangeStart)
        .queryParam("eventCreatedDateTime:lt", rangeEnd)
        .get(EVENTS)
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        // The test data includes 2 shipment events for this case. Given the narrow date range, it
        // seems acceptable to
        // validate an exact match.  Note the strict match is used to validate that the TZ
        // conversion works correctly
        // when filtering
        .body("size()", equalTo(2))
        .body("eventClassifierCode", everyItem(anyOf(equalTo("ACT"), equalTo("EST"))))
        .body(
            "eventCreatedDateTime",
            everyItem(
                asDateTime(
                    allOf(
                        greaterThanOrEqualTo(ZonedDateTime.parse(rangeStart)),
                        lessThan(ZonedDateTime.parse(rangeEnd))))))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  @Test
  void testGetAllEventsAndHeaders() {
    given()
        .contentType("application/json")
        .queryParam("limit", 1)
        .get(EVENTS)
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .header("API-Version", equalTo("1.1.0"))
        .header("Current-Page", matchesRegex("^https?://.*/v1/events\\?cursor=[a-zA-Z\\d=]*$"))
        .header("First-Page", matchesRegex("^https?://.*/v1/events\\?cursor=[a-zA-Z\\d=]*$"))
        .header("Next-Page", matchesRegex("^https?://.*/v1/events\\?cursor=[a-zA-Z\\d=]*$"))
        .body("size()", greaterThanOrEqualTo(0))
        .body("eventType", everyItem(equalTo("OPERATIONS")))
        .body(jsonSchemaValidator("operationsEvent"));
  }

  /**
   * Convert the input (assumed to be String) into a ZonedDateTime before chaining off to the next
   * match
   *
   * <p>The conversion will use {@link ZonedDateTime#parse(CharSequence)}. If the parsing fails, the
   * value is assumed not to match.
   *
   * @param dateTimeMatcher The matcher that should operator on a ZonedDateTime
   * @return The combined matcher
   */
  // Use ChronoZonedDateTime as bound to avoid fighting generics with lessThan that "reduces"
  // ZonedDateTime
  // to the ChronoZonedDateTime (via Comparable)
  private static <T extends ChronoZonedDateTime<?>> Matcher<T> asDateTime(
      Matcher<T> dateTimeMatcher) {
    return new DateTimeMatcher<>(dateTimeMatcher);
  }

  @RequiredArgsConstructor
  private static class DateTimeMatcher<T extends ChronoZonedDateTime<?>> extends BaseMatcher<T> {

    private final Matcher<T> matcher;

    @Override
    public boolean matches(Object actual) {
      ZonedDateTime dateTime;
      if (!(actual instanceof String)) {
        return false;
      }
      try {
        dateTime = ZonedDateTime.parse((String) actual);
      } catch (DateTimeParseException e) {
        return false;
      }
      return matcher.matches(dateTime);
    }

    @Override
    public void describeTo(Description description) {
      description.appendText("as datetime ").appendDescriptionOf(matcher);
    }
  }
}
