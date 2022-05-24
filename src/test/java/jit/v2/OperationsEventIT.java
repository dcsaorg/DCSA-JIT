package jit.v2;

import io.restassured.http.ContentType;
import jit.config.TestConfig;
import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.enums.DocumentTypeCode;
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
import static jit.config.TestConfig.jsonSchemaValidator;
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
  void testGetAllEventsByOperationsEventTypeCode() {
    BiConsumer<String, Matcher<String>> runner = (s, m) ->
        given()
        .contentType("application/json")
        .queryParam("operationsEventTypeCode", s)
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("size()", greaterThanOrEqualTo(0))
        .body("eventType", everyItem(equalTo("SHIPMENT")))
        .body("eventClassifierCode", everyItem(equalTo("ACT")))
        .body("operationsEventTypeCode", everyItem(m))
        .body(jsonSchemaValidator("operationsEvent"))
    ;

    runner.accept("APPR,ISSU", anyOf(equalTo("APPR"), equalTo("ISSU")));
    runner.accept("APPR", equalTo("APPR"));
    runner.accept("ISSU", equalTo("ISSU"));
  }

  @Test
  void testGetAllEventsByDocumentTypeCodeCode() {
    BiConsumer<String, Matcher<String>> runner = (s, m) ->
      given()
        .contentType("application/json")
        .queryParam("documentTypeCode", s)
        .get("/v1/events")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("size()", greaterThanOrEqualTo(0))
        .body("eventType", everyItem(equalTo("SHIPMENT")))
        .body("eventClassifierCode", everyItem(equalTo("ACT")))
        .body("documentTypeCode", everyItem(m))
//        .body(jsonSchemaValidator("operationsEvent"))
      ;
    runner.accept("SHI,TRD", anyOf(equalTo("SHI"), equalTo("TRD")));
    runner.accept("SHI", equalTo("SHI"));
    runner.accept("TRD", equalTo("TRD"));
  }

  @Test
  void testGetAllEventsByCombinedQuery() {
    given()
      .contentType("application/json")
      // VOID only applies to TRD, so this is guaranteed to give 0 matches.
      .queryParam("documentTypeCode", "SHI")
      .queryParam("operationsEventTypeCode", "VOID")
      .get("/v1/events")
      .then()
      .assertThat()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("size()", equalTo(0))
      .body(jsonSchemaValidator("operationsEvent"))
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
      .queryParam("transportCallID", "7f2d833c-2c7f-4fc5-a71a-e510881da64a")
      .get("/v1/events")
      .then()
      .assertThat()
      .statusCode(200)
      .contentType(ContentType.JSON)
      // The test data includes at least 3 shipment events related to the reference. But something adding additional
      // events.
      .body("size()", greaterThanOrEqualTo(1))
      .body("eventType", everyItem(equalTo(EventType.OPERATIONS.toString())))
      .body("eventClassifierCode", everyItem(equalTo(EventClassifierCode.EST.toString())))
//      .body(jsonSchemaValidator("operationsEvent"))
    ;
  }

  @Test
  void testGetAllEventsByCarrierBookingReferenceWithEventCreatedDateTimeRange() {
    String rangeStart = "2021-01-08T00:00:00Z";
    String rangeEnd = "2021-01-09T00:00:00Z";
    given()
      .contentType("application/json")
      .queryParam("transportCallID", "7f2d833c-2c7f-4fc5-a71a-e510881da64a")
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
      .body("eventType", everyItem(equalTo("SHIPMENT")))
      .body("eventClassifierCode", everyItem(equalTo("ACT")))
      .body("documentTypeCode", everyItem(anyOf(equalTo("SHI"), equalTo("TRD"))))
      .body("eventCreatedDateTime", everyItem(
        asDateTime(
          allOf(
            greaterThanOrEqualTo(ZonedDateTime.parse(rangeStart)),
            lessThan(ZonedDateTime.parse(rangeEnd))
      ))))
      .body("documentReferences.flatten().findAll { it.documentReferenceType == 'BKG' }.size()", greaterThanOrEqualTo(3))
      .body("documentReferences.flatten().findAll { it.documentReferenceType == 'BKG' }.documentReferenceValue", everyItem(equalTo("832deb4bd4ea4b728430b857c59bd057")))
      .body(jsonSchemaValidator("operationsEvent"))
    ;
  }

  @Test
  void testGetAllEventsByEventCreatedDateTimeRange() {
    String rangeStart = "2021-01-08T00:00:00Z";
    // 10:00-0400 is 14:00 at Z, so the first event for CBR 832deb4bd4ea4b728430b857c59bd057 is included while the
    // latter to are excluded
    String rangeEnd = "2021-01-08T10:00:00-04:00";
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
      .body("eventClassifierCode", everyItem(equalTo(EventClassifierCode.EST.toString())))
      .body("documentTypeCode", everyItem(anyOf(equalTo("SHI"), equalTo("TRD"))))
      .body("eventCreatedDateTime", everyItem(
        asDateTime(
          allOf(
            greaterThanOrEqualTo(ZonedDateTime.parse(rangeStart)),
            lessThan(ZonedDateTime.parse(rangeEnd))
      ))))
      .body("documentReferences.flatten().findAll { it.documentReferenceType == 'BKG' }.size()", greaterThanOrEqualTo(1))
      .body("documentReferences.flatten().findAll { it.documentReferenceType == 'BKG' }.documentReferenceValue", everyItem(equalTo("832deb4bd4ea4b728430b857c59bd057")))
      .body(jsonSchemaValidator("operationsEvent"))
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
