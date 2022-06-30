package org.dcsa.jit.itests.v1;

import org.dcsa.jit.itests.config.RestAssuredConfigurator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.dcsa.jit.itests.config.RestAssuredConfigurator.*;
import static org.dcsa.jit.itests.config.RestAssuredConfigurator.TIMESTAMPS;
import static org.dcsa.jit.itests.config.TestUtil.*;


/*
 * Tests related to the POST /Timestamps endpoint
 */
public class PostTimestampsIT {
  public static final String VALID_TIMESTAMP =
      loadFileAsString(
          "TimestampSample.json");

  @BeforeAll
  static void configs() {
    RestAssuredConfigurator.initialize();
  }

  // Testing with all fields provided in VALID_TIMESTAMP variable
  @Test
  public void testTimestampAllParameters() {
    given()
        .contentType("application/json")
        .header("testname", "testTimestampRequiredParameters")
        .body(VALID_TIMESTAMP)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(204);
  }

  // Testing with all fields provided in VALID_TIMESTAMP variable
  @Test
  public void testTimestampNoVesselObject() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
    map.remove("vessel");
    given()
        .contentType("application/json")
        .header("testname", "testTimestampRequiredParameters")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(204);
  }

  // Testing with no fields / Empty body
  // Should fail as nothing is provided
  @Test
  public void testTransportCallsNoMandatoryParameters() {
    given()
        .contentType("application/json")
        .header("testname", "testTransportCallsNoMandatoryParameters")
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    given()
        .contentType("application/json")
        .header("testname", "testTransportCallsNoMandatoryParameters2")
        .body("")
        . // Note: Empty body
        post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);
  }

  // Testing with mandatory fields + FacilitySMDGCode field
  @Test
  public void testFacilitySMDGCodeField() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
    map.remove("eventLocation");
    map.remove("vesselPosition");
    map.remove("modeOfTransport");
    map.remove("portCallServiceTypeCode");

    given()
        .contentType("application/json")
        .header("testname", "testFacilitySMDGCodeField")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(204);
  }

  // Test // Testing with mandatory fields + FacilitySMDGCode field
  // Should fail because specification -> maxLength:6
  @Test
  public void testFacilitySMDGCodeFieldFalseFormat() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
    map.remove("eventLocation");
    map.remove("vesselPosition");
    map.remove("modeOfTransport");
    map.remove("portCallServiceTypeCode");

    map.put("facilitySMDGCode", "aBCDaFGHaE222");

    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);
  }

  // Testing with mandatory fields + EventLocation field
  @Test
  public void testEventLocationField() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
    map.remove("facilitySMDGCode");
    map.remove("vesselPosition");
    map.remove("modeOfTransport");
    map.remove("portCallServiceTypeCode");

    given()
        .contentType("application/json")
        .header("testname", "testEventLocationField")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(204);
  }

  // Testing with mandatory fields + EventLocation field
  // fails as eventLocation is an object.
  @Test
  public void testEventLocationFalseFormat() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
    map.remove("vesselPosition");
    map.remove("modeOfTransport");
    map.remove("portCallServiceTypeCode");

    map.put("eventLocation", "aBCDaFGHaE"); // Wrong format - eventLocation is an object

    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);
  }

  // Testing with mandatory fields + VesselPosition field
  @Test
  public void testVesselPositionField() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
    map.remove("modeOfTransport");
    map.remove("eventLocation");
    map.remove("portCallServiceTypeCode");

    given()
        .contentType("application/json")
        .header("testname", "testVesselPositionField")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(204);
  }

  // Testing with mandatory fields + vesselPosition field
  // fails as vesselPosition is an object & (latitude & longitude are required parameters).
  @Test
  public void testVesselPositionFalseFormat() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
    map.remove("modeOfTransport");
    map.remove("eventLocation");
    map.remove("portCallServiceTypeCode");

    map.put("vesselPosition", "aBCDaFGHaE"); // Wrong format

    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

  }

  // Testing with mandatory fields + ModeOfTransport field
  @Test
  public void testModeOfTransportField() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
    map.remove("vesselPosition");
    map.remove("eventLocation");
    map.remove("portCallServiceTypeCode");

    given()
        .contentType("application/json")
        .header("testname", "testModeOfTransportField")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(204);
  }

  // Testing with mandatory fields + ModeOfTransport field
  // fails as ModeOfTransport is an ENUM.
  @Test
  public void testModeOfTransportFieldFalseFormat() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
    map.remove("facilitySMDGCode");
    map.remove("modeOfTransport");
    map.remove("eventLocation");
    map.remove("portCallServiceTypeCode");

    map.put("modeOfTransport", "VES"); // Wrong format ( VES -- is not ENUM)

    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    map.put("modeOfTransport", ""); // Empty (fails - not ENUM)

    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);
  }

  // Testing with mandatory fields + PortCallServiceTypeCode field
  @Test
  public void testPortCallServiceTypeCodeField() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
    map.remove("vesselPosition");
    map.remove("eventLocation");
    map.remove("modeOfTransport");

    given()
        .contentType("application/json")
        .header("testname", "testPortCallServiceTypeCodeField")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(204);
  }

  // Testing with mandatory fields + PortCallServiceTypeCode field
  // fails as portCallServiceTypeCode is an ENUM.
  // Test modeOfTransport,
  @Test
  public void testPortCallServiceTypeCodeFalseFormat() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
    map.remove("facilitySMDGCode");
    map.remove("modeOfTransport");
    map.remove("eventLocation");
    map.remove("modeOfTransport");

    map.put("portCallServiceTypeCode", "VES"); // Wrong format ( VES -- is not ENUM)

    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    map.put("portCallServiceTypeCode", ""); // Empty (fails - not ENUM)

    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);
  }

  // Testing with mandatory fields + OPTIONAL transportCallSequenceNumber field
  @Test
  public void testTransportCallSequenceNumberField() {

    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
    map.remove("facilitySMDGCode");
    map.remove("modeOfTransport");
    map.remove("eventLocation");
    map.remove("modeOfTransport");
    map.remove("portCallServiceTypeCode");

    given()
        .contentType("application/json")
        .header("testname", "testPortCallTransportCallSequenceNumberField")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(204);
  }

  // Testing with mandatory fields + transportCallSequenceNumber field
  // Fails as we post a similar timestamp with only difference being the transportCallSequenceNumber
  // set to 1
  // Thus, the Ambiguous transport call error is returned.
  @Test
  public void testTransportCallSequenceNumberFieldFalseFormat() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

    // Post duplicate timestamp with different transportCallSequenceNumber
    map.remove("transportCallSequenceNumber");
    map.put("transportCallSequenceNumber", 1);
    given()
        .contentType("application/json")
        .header("testname", "testTransportCallSequenceNumberFieldFalseFormat")
        .body(map)
        .post(TIMESTAMPS);

    // Show that error is returned when transportCallSequenceNumber is not given.
    map.remove("transportCallSequenceNumber");
    given()
        .contentType("application/json")
        .header("testname", "testTransportCallSequenceNumberFieldFalseFormat")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400)
        .body(jsonSchemaValidator("error"))
        .body(
            "errors.collect { it.message }",
            Matchers.hasItem(Matchers.containsString("Ambiguous transport call")));
  }

  // Testing with mandatory field Publisher with only identifyingCodes
  @Test
  public void testMandatoryPublisherFieldWithOnlyIdentifyingCodes() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

    map.remove("publisher");
    Map<String, List<Map<String, String>>> identifyingCodes =
        Map.of(
            "identifyingCodes",
            List.of(Map.of("DCSAResponsibleAgencyCode", "SMDG", "partyCode", "MSK")));
    map.put("publisher", identifyingCodes);

    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(204);
  }

  // Testing with mandatory fields - Except PublisherField field
  // Should fail as PublisherField is mandatory
  @Test
  public void testMandatoryPublisherFieldFalseFormat() {

    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

    map.remove("publisher");
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // Wrong format Publisher parameter
    map.put("publisher", "{sasds}");
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // Empty Publisher parameter
    map.put("publisher", "{}"); // Empty
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // Wrong format
    map.put("publisher", ""); // Empty
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);
  }

  // Testing with mandatory fields - Except publisherRole field
  // Should fail as publisherRole is mandatory
  @Test
  public void testMandatoryPublisherRoleFieldFalseFormat() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

    map.remove("publisherRole");
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // Empty
    map.put("publisherRole", ""); // Empty
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);
  }

  // Testing with mandatory fields - Except vesselIMONumber field
  // Should fail as vesselIMONumber is mandatory
  @Test
  public void testMandatoryVesselIMONumberFieldFalseFormat() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

    map.remove("vesselIMONumber");
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // wrong format maxLength:7
    map.put("vesselIMONumber", "abcdfght");
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // Empty
    map.put("vesselIMONumber", ""); // Empty
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);
  }

  // Testing with mandatory fields - Except UNLocationCode field
  // Should fail as UNLocationCode is mandatory
  @Test
  public void testMandatoryUNLocationCodeFieldFalseFormat() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

    map.remove("UNLocationCode");
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // wrong format maxLength:5
    map.put("UNLocationCode", "abcdfght");
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // Empty value
    map.put("UNLocationCode", ""); // Empty
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);
  }

  // Testing with mandatory fields - Except FacilityTypeCode field
  // Should fail as FacilityTypeCode is mandatory
  @Test
  public void testMandatoryFacilityTypeCodeFieldFalseFormat() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

    map.remove("facilityTypeCode");
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // wrong format -> ENUM
    map.put("facilityTypeCode", "abcdfght");
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // Empty value
    map.put("facilityTypeCode", ""); // Empty
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);
  }

  // Testing with mandatory fields - Except EventClassifierCode field
  // Should fail as EventClassifierCode is mandatory
  @Test
  public void testMandatoryEventClassifierCodeFieldFalseFormat() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

    map.remove("eventClassifierCode");
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // Empty value
    map.put("eventClassifierCode", ""); // Empty
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);
  }

  // Testing with mandatory fields - Except OperationsEventTypeCode field
  // Should fail as OperationsEventTypeCode is mandatory
  @Test
  public void testMandatoryOperationsEventTypeCodeFieldFalseFormat() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

    map.remove("operationsEventTypeCode");
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // Empty value
    map.put("operationsEventTypeCode", ""); // Empty
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);
  }

  // Testing with mandatory fields - Except EventDateTime field
  // Should fail as EventDateTime is mandatory
  @Test
  public void testMandatoryEventDateTimeFieldFalseFormat() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

    map.remove("eventDateTime");
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // Wrong value
    map.put("eventDateTime", "sdf"); // wrong format
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);

    // Empty value
    map.put("eventDateTime", ""); // Empty
    given()
        .contentType("application/json")
        .body(map)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(400);
  }
}
