package org.dcsa.jit.itests.v1;

import org.dcsa.jit.itests.config.RestAssuredConfigurator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.dcsa.jit.itests.config.RestAssuredConfigurator.TIMESTAMPS;
import static org.dcsa.jit.itests.config.TestUtil.*;


/*
 * Tests related to the POST /Timestamps endpoint
 * NOTE:  VALID_TIMESTAMP_1_0 & VALID_TIMESTAMP_1_1 target the timestamp definition ETA-BERTH
 * Additionally, we test aggressively for latest timestamp version
 * & testing for backwards compatibility is done by posting an earlier valid timestamp with all optional fields given.
 * Finally, custom tests raised in specific tickets are included (with their ticket number)
 * */
public class PostTimestampsIT {

  public static final String VALID_TIMESTAMP_1_2 =
    loadFileAsString("TimestampSample_v1-2.json");
  public static final String VALID_TIMESTAMP_1_1 =
      loadFileAsString("TimestampSample_v1-1.json");
  public static final String VALID_TIMESTAMP_1_0 =
    loadFileAsString("TimestampSample_v1-0.json");

  @BeforeAll
  static void configs() {
    RestAssuredConfigurator.initialize();
  }

  @Test
  public void testTimestampAllParametersv1dot2() {
    given()
      .contentType("application/json")
      .header("testname", "testTimestampRequiredParameters")
      .body(VALID_TIMESTAMP_1_2)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(204);
  }

  // Testing with all fields provided in VALID_TIMESTAMP_1_0 variable
  @Test
  public void testTimestampAllParametersv1dot0() {
    given()
      .contentType("application/json")
      .header("testname", "testTimestampRequiredParameters")
      .body(VALID_TIMESTAMP_1_0)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(204);
  }

  // Testing with all fields provided in VALID_TIMESTAMP_1_1 variable
  @Test
  public void testTimestampAllParameters() {
    given()
        .contentType("application/json")
        .header("testname", "testTimestampRequiredParameters")
        .body(VALID_TIMESTAMP_1_1)
        .post(TIMESTAMPS)
        .then()
        .assertThat()
        .statusCode(204);
  }

  @Test
  public void testInvalidTimestampMixJit11vsJit12() {
    given()
      .contentType("application/json")
      .header("testname", "testTimestampRequiredParameters")
      .body(loadFileAsString("InvalidTimestampSample_mix_v12x11.json"))
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(400);
  }


  // This test addresses concerns raised in ticket DDT-1052
  @Test
  @SuppressWarnings("unchecked")
  public void testCustomTimestampDDTdot1052() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_0);
    // below is done to address given swagger spec example
    Map<String, Object> eventLocation = (Map<String, Object>) map.get("eventLocation");
    eventLocation.remove("address");
    eventLocation.remove("locationName");
    eventLocation.remove("latitude");
    eventLocation.remove("longitude");
    map.put("eventLocation", eventLocation);

    Map<String, Object> partyNameAndIdentifyingCodes =
      Map.of(
        "identifyingCodes",
        List.of(Map.of("DCSAResponsibleAgencyCode", "SMDG",
      "partyCode", "MSK",
      "codeListName", "LCL"
        )),
        "partyName", "Maersk"
        );
    map.put("publisher", partyNameAndIdentifyingCodes);
    map.remove("facilitySMDGCode");
    map.remove("delayReasonCode");
    map.remove("remark");

    given()
      .contentType("application/json")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(204);
  }

  // Testing with all fields provided in VALID_TIMESTAMP_1_1 variable except vessel object
  @Test
  public void testTimestampNoVesselObject() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
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


  // Test conditional mandatory'ness of facilityCode
  @Test
  public void testFacilityCodeCheck() {

    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
    map.remove("facilitySMDGCode");
    map.remove("modeOfTransport");
    map.remove("eventLocation");
    map.remove("modeOfTransport");
    map.remove("portCallServiceTypeCode");

    given()
      .contentType("application/json")
      .header("testname", "testFacilityCodeCheck_ShouldBePresent")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(400);

    map = jsonToMap(VALID_TIMESTAMP_1_1);
    map.put("facilityTypeCode", "PBPL"); // PBPL must not have a facility
    map.remove("modeOfTransport");
    map.remove("eventLocation");
    map.remove("modeOfTransport");
    map.remove("portCallServiceTypeCode");

    given()
      .contentType("application/json")
      .header("testname", "testFacilityCodeCheck_ShouldNotBePresent")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(400);
  }

  @Test
  public void testMilesToDestConditionalExclusion() {

    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
    map.put("milesToDestinationPort", 51.1);

    // Allowed for ETA-Berth
    given()
      .contentType("application/json")
      .header("testname", "testMilesToDestConditionalExclusion_Permitted")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(204);


    // Not allowed for RTA-Berth
    map.put("eventClassifierCode", "REQ");
    given()
      .contentType("application/json")
      .header("testname", "testMilesToDestConditionalExclusion_NotPermitted")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(400);
  }

  // Testing with mandatory fields + OPTIONAL transportCallSequenceNumber field
  @Test
  public void testTransportCallSequenceNumberField() {

    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);
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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);

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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);

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

    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);

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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);

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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);

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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);

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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);

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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);

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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);

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
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);

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

  // Test Logic for the ImportVoyageNumber & ExportVoyageNumber & carrierVoyageNumber fields
  // should fail when all 3 are present or absent
  @Test
  public void testLogicForVoyageNumberFields11() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_1);

    // add to test that when carrierVoyageNumber != (exportVoyageNumber || importVoyageNumber)
    map.put("carrierVoyageNumber", "sdf");
    given()
      .contentType("application/json")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(400);

    // remove exportVoyageNumber to test that when(exportVoyageNumber & importVoyageNumber) are not given together.
    map.remove("exportVoyageNumber");
    given()
      .contentType("application/json")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(400);

    // Here we remove importVoyageNumber as well, thus we have a valid timestamp
    map.remove("importVoyageNumber");
    map.put("carrierVoyageNumber", "2103S"); // valid carrierVoyageNumber value
    given()
      .contentType("application/json")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(204);
  }

  @Test
  public void testLogicForVoyageNumberFields12() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_2);

    // add to test that when carrierVoyageNumber != (carrierExportVoyageNumber || carrierImportVoyageNumber)
    map.put("carrierVoyageNumber", "sdf");
    given()
      .contentType("application/json")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(400);

    // remove carrierExportVoyageNumber to test that when(carrierExportVoyageNumber & carrierImportVoyageNumber) are not given together.
    map.remove("carrierExportVoyageNumber");
    given()
      .contentType("application/json")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(400);

    // Here we remove carrierImportVoyageNumber as well, thus we have a valid timestamp
    map.remove("carrierImportVoyageNumber");
    map.put("carrierVoyageNumber", "2103S"); // valid carrierVoyageNumber value
    given()
      .contentType("application/json")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(204);
  }

  /* If the case when vessel.vesselIMONumber is different than vesselIMONumber.
   */
  @Test
  public void testLogicForVesselIMONumber() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_2);

    ((Map<String,String>) map.get("vessel")).put("vesselIMONumber", "sdf");
    given()
      .contentType("application/json")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(400);
  }

  /* This test addresses a scenario raised in ticket DDT-1005
   * Timestamp definition -> ETS-Cargo Ops (<implicit>)
   * test uses JIT 1.0 timestamp spec
   */
  @Test
  public void testJIT1dot0TimestampTimestampTypeIsETSCARGO() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_0);


    Map<String, Object> partyNameAndIdentifyingCodes =
      Map.of(
        "identifyingCodes",
        List.of(Map.of("DCSAResponsibleAgencyCode", "SMDG",
          "partyCode", "MSK",
          "codeListName", "LCL"
        )),
        "partyName", "ETS-Cargo Ops (<implicit>)"   // We set party name as Timestamp definition
        // To allow for easier look up of posted Timestamp/operationsEvent
      );
    map.put("publisher", partyNameAndIdentifyingCodes);
    // below aligns fields to the timestamp definition
    map.put("facilityTypeCode", "BRTH");
    map.put("eventClassifierCode", "EST");
    map.put("operationsEventTypeCode", "STRT");
    map.put("portCallServiceTypeCode", "CRGO");

    given()
      .contentType("application/json")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(204);

  }

  /* This test addresses a scenario raised in ticket DDT-1005
   * Timestamp definition -> ATS-Pilotage (<implicit>)
   * test uses JIT 1.0 timestamp spec
   */
  @Test
  public void testJIT1dot0TimestampTimestampDefinitionIsATSPILO() {
    Map<String, Object> map = jsonToMap(VALID_TIMESTAMP_1_0);

    Map<String, Object> partyNameAndIdentifyingCodes =
      Map.of(
        "identifyingCodes",
        List.of(Map.of("DCSAResponsibleAgencyCode", "SMDG",
          "partyCode", "MSK",
          "codeListName", "LCL"
        )),
        "partyName", "ATS-Pilotage (<implicit>)"   // We set party name as Timestamp definition
        // To allow for easier look up of posted Timestamp/operationsEvent
      );
    map.put("publisher", partyNameAndIdentifyingCodes);
    // below aligns fields to the timestamp definition
    map.put("facilityTypeCode", "PBPL");
    map.put("eventClassifierCode", "ACT");
    map.put("operationsEventTypeCode", "STRT");
    map.put("portCallServiceTypeCode", "PILO");
    // PBPL timestamps do not have facilities.
    map.remove("facilitySMDGCode");
    Map<String, Object> location = (Map<String, Object>) map.get("eventLocation");
    location.remove("facilityCode");
    location.remove("facilityCodeListProvider");

    given()
      .contentType("application/json")
      .body(map)
      .post(TIMESTAMPS)
      .then()
      .assertThat()
      .statusCode(204);
  }
}
