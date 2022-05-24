package jit.v2;

import io.restassured.http.ContentType;
import jit.config.TestConfig;
import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.enums.EventClassifierCode;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.skernel.model.enums.PartyFunction;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.function.BiConsumer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class OperationsEventIT {

  @BeforeAll
  static void configs() throws IOException {
    TestConfig.init();
  }

  @Test
  void testGetAllEventsAndHeaders() {
    given()
      .contentType("application/json")
      .queryParam("limit", 1)
      .get("/v1/events")
      .then()
      .assertThat()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .header("API-Version", equalTo("1.0.0"))
      .header("Current-Page", matchesRegex("^https?://.*/v1/events\\?cursor=[a-zA-Z\\d]*$"))
      .header("Next-Page", matchesRegex("^https?://.*/v1/events\\?cursor=[a-zA-Z\\d]*$"))
      .header("Last-Page", matchesRegex("^https?://.*/v1/events\\?cursor=[a-zA-Z\\d]*$"))
      .body("size()", greaterThanOrEqualTo(0))
      .body("eventType", everyItem(equalTo("OPERATIONS")))
      .body("eventClassifierCode", everyItem(equalTo("ACT")))
//      .body(jsonSchemaValidator("operationsEvent"))
    ;
  }

  @Test
  void testGetAllEventsByExportVoyageNumber() {
    BiConsumer<String, Matcher<String>> runner = (s, m) ->
        given()
        .contentType("application/json")
        .queryParam("exportVoyageNumber", s)
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("size()", equalTo(1))
        .body("eventType", everyItem(equalTo(EventType.OPERATIONS.toString())))
        .body("eventClassifierCode", everyItem(anyOf(equalTo(EventClassifierCode.EST.toString()), equalTo(EventClassifierCode.ACT.toString()))))
        .body("operationsEventTypeCode", everyItem(m))
//        .body(jsonSchemaValidator("operationsEvent"))
    ;

    runner.accept("A_carrier_voyage_number", equalTo("ARRI"));
    runner.accept("TNT1E", equalTo("DEPA"));
  }

  @Test
  void testGetAllEventsByImportVoyageNumber() {
    BiConsumer<String, Matcher<String>> runner = (s, m) ->
      given()
        .contentType("application/json")
        .queryParam("importVoyageNumber", s)
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("size()", equalTo(1))
        .body("eventType", everyItem(equalTo(EventType.OPERATIONS.toString())))
        .body("eventClassifierCode", everyItem(anyOf(equalTo(EventClassifierCode.EST.toString()), equalTo(EventClassifierCode.ACT.toString()))))
        .body("operationsEventTypeCode", everyItem(m))
//        .body(jsonSchemaValidator("operationsEvent"))
      ;

    runner.accept("A_carrier_voyage_number", equalTo("ARRI"));
    runner.accept("TNT1E", equalTo("DEPA"));
  }

  @Test
  void testGetAllEventsByUNLocationCode() {
      String str = given()
        .contentType("application/json")
        .queryParam("unLocationCode", "USNYC")
        .get("/v1/events")
        .then().extract().asString();
    System.out.println(str);
      str = given()
        .contentType("application/json")
        .queryParam("unLocationCode", "SGSIN")
        .get("/v1/events")
        .then().extract().asString();
    System.out.println(str);

    BiConsumer<String, Matcher<String>> runner = (s, m) ->
      given()
        .contentType("application/json")
        .queryParam("unLocationCode", s)
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("size()", equalTo(1))
        .body("eventType", everyItem(equalTo(EventType.OPERATIONS.toString())))
        .body("eventClassifierCode", everyItem(anyOf(equalTo("ACT"), equalTo("EST"))))
        .body("operationsEventTypeCode", everyItem(m))
//        .body(jsonSchemaValidator("operationsEvent"))
      ;

    runner.accept("SGSIN", equalTo("DEPA"));
    runner.accept("USNYC", equalTo("ARRI"));
  }

  @Test
  void testGetAllEventsByCombinedQuery() {
    given()
      .contentType("application/json")
      .queryParam("exportVoyageNumber", "TNT1E")
      .queryParam("UNLocationCode", "SGSIN")
      .get("/v1/events")
      .then()
      .assertThat()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("size()", equalTo(1))
//      .body(jsonSchemaValidator("operationsEvent"))
    ;
  }

  @Test
  void testGetAllEventsByCarrierServiceCode() {
    given()
        .contentType("application/json")
        .queryParam("carrierServiceCode", "A_CSC")
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        // The test data includes at least 3 shipment events related to the reference. But something
        // adding additional
        // events.
        .body("size()", greaterThanOrEqualTo(1))
        .body("eventType", everyItem(equalTo(EventType.OPERATIONS.toString())))
        .body("eventClassifierCode", everyItem(equalTo(EventClassifierCode.EST.toString())))
        .body("publisherRole", everyItem(equalTo(PartyFunction.CA.toString())))
        .body("publisher.partyName", everyItem(equalTo("Asseco Denmark")))
//        .body(jsonSchemaValidator("operationsEvent"))
    ;
  }

  @Test
  void testGetAllEventsByTransportCallID() {
    given()
      .contentType("application/json")
      .queryParam("transportCallID", "b785317a-2340-4db7-8fb3-c8dfb1edfa60")
      .get("/v1/events")
      .then()
      .assertThat()
      .statusCode(200)
      .contentType(ContentType.JSON)
      // The test data includes at least 3 shipment events related to the reference. But something adding additional
      // events.
      .body("size()", greaterThanOrEqualTo(1))
      .body("eventType", everyItem(equalTo(EventType.OPERATIONS.toString())))
      .body("eventClassifierCode", everyItem(equalTo(EventClassifierCode.ACT.toString())))
//      .body(jsonSchemaValidator("operationsEvent"))
    ;
  }

  @Test
  void testGetAllEventsByTransportCallIDWithEventCreatedDateTimeRange() {
    String rangeStart = "2022-05-08T00:00:00Z";
    String rangeEnd = "2022-05-09T00:00:00Z";
    given()
      .contentType("application/json")
      .queryParam("transportCallID", "b785317a-2340-4db7-8fb3-c8dfb1edfa60")
      .queryParam("eventCreatedDateTime:gte", rangeStart)
      .queryParam("eventCreatedDateTime:lt", rangeEnd)
      .get("/v1/events")
      .then()
      .assertThat()
      .statusCode(200)
      .contentType(ContentType.JSON)
      // The test data includes 3 shipment events for this case. Given the narrow date range, it seems acceptable to
      // validate an exact match.
      .body("size()", equalTo(1))
      .body("eventType", everyItem(equalTo(EventType.OPERATIONS.toString())))
      .body("eventClassifierCode", everyItem(equalTo(EventClassifierCode.ACT.toString())))
      .body("eventCreatedDateTime", everyItem(
        asDateTime(
          allOf(
            greaterThanOrEqualTo(ZonedDateTime.parse(rangeStart)),
            lessThan(ZonedDateTime.parse(rangeEnd))
      ))))
//      .body(jsonSchemaValidator("operationsEvent"))
    ;
  }

  @Test
  void testGetAllEventsByEventCreatedDateTimeRange() {
    String rangeStart = "2022-02-08T00:00:00Z";
    // 10:00-0400 is 14:00 at Z, so the first event for CBR 832deb4bd4ea4b728430b857c59bd057 is included while the
    // latter to are excluded
    String rangeEnd = "2022-05-09T10:00:00-04:00";
    given()
      .contentType("application/json")
      .queryParam("eventCreatedDateTime:gte", rangeStart)
      .queryParam("eventCreatedDateTime:lt", rangeEnd)
      .get("/v1/events")
      .then()
      .assertThat()
      .statusCode(200)
      .contentType(ContentType.JSON)
      // The test data includes 2 shipment events for this case. Given the narrow date range, it seems acceptable to
      // validate an exact match.  Note the strict match is used to validate that the TZ conversion works correctly
      // when filtering
      .body("size()", equalTo(2))
      .body("eventType", everyItem(equalTo(EventType.OPERATIONS.toString())))
      .body("eventClassifierCode", everyItem(anyOf(equalTo("ACT"), equalTo("EST"))))
      .body("eventCreatedDateTime", everyItem(
        asDateTime(
          allOf(
            greaterThanOrEqualTo(ZonedDateTime.parse(rangeStart)),
            lessThan(ZonedDateTime.parse(rangeEnd))
      ))))
//      .body(jsonSchemaValidator("operationsEvent"))
    ;
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
