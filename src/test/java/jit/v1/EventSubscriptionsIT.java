package jit.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.Response;
import jit.config.TestConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static jit.config.TestConfig.EVENT_SUBSCRIPTIONS;
import static jit.config.TestConfig.jsonSchemaValidator;
import static jit.config.TestUtil.loadFileAsString;
/*
Test for /event-subscriptions
 */
public class EventSubscriptionsIT {
  public static final String VALID_EVENT_SUBSCRIPTION =
      loadFileAsString("ValidEventSubscriptionSample.json");

  final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeAll
  static void configs() throws IOException {
    TestConfig.init();
  }

  @Test
  public void testToCreateEventSubscriptionWithInvalidEventType() {
    final String INVALID_EVENT_SUBSCRIPTION =
        loadFileAsString("InvalidEventTypeEventSubscriptionSample.json");

    given()
        .contentType("application/json")
        .body(INVALID_EVENT_SUBSCRIPTION)
        .post(EVENT_SUBSCRIPTIONS)
        .then()
        .assertThat()
        .statusCode(400)
        .body(jsonSchemaValidator("error"))
        .body(
            "errors.collect { it.message }",
            Matchers.hasItem(Matchers.containsString("must be any of [OPERATIONS]")));
  }

  @Test
  public void testToCreateEventSubscriptionWithInvalidVesselIMONumber() {
    final String INVALID_EVENT_SUBSCRIPTION =
        loadFileAsString("InvalidVesselIMONumberEventSubscriptionSample.json");

    given()
        .contentType("application/json")
        .body(INVALID_EVENT_SUBSCRIPTION)
        .post(EVENT_SUBSCRIPTIONS)
        .then()
        .assertThat()
        .statusCode(400)
        .body(jsonSchemaValidator("error"))
        .body(
            "errors.collect { it.message }",
            Matchers.hasItem(Matchers.containsString("must be a valid Vessel IMO Number")));
  }

  @Test
  public void testToCreateEventSubscription() {
    Response response =
        given()
            .contentType("application/json")
            .body(VALID_EVENT_SUBSCRIPTION)
            .post(EVENT_SUBSCRIPTIONS);

    assert response.getStatusCode() == 201;
    assert response.jsonPath().get("subscriptionID") != null;
  }

  @Test
  public void testToGetEventSubscriptions() {

    given()
        .contentType("application/json")
        .get(EVENT_SUBSCRIPTIONS)
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  public void testToGetSpecificEventSubscription() {
    Response response =
        given()
            .contentType("application/json")
            .body(VALID_EVENT_SUBSCRIPTION)
            .post(EVENT_SUBSCRIPTIONS);

    String createdEventSubscriptionID = response.jsonPath().get("subscriptionID");

    given()
        .contentType("application/json")
        .get(EVENT_SUBSCRIPTIONS +"/"+ createdEventSubscriptionID)
        .then()
        .assertThat()
        .statusCode(200)
        .body("subscriptionID", Matchers.equalTo(createdEventSubscriptionID));
  }

  @Disabled
  @Test
  public void testToDeleteSpecificEventSubscription() {
    Response response =
        given()
            .contentType("application/json")
            .body(VALID_EVENT_SUBSCRIPTION)
            .post(EVENT_SUBSCRIPTIONS);

    String createdEventSubscriptionID = response.jsonPath().get("subscriptionID");

    given()
        .contentType("application/json")
        .delete(EVENT_SUBSCRIPTIONS +"/"+ createdEventSubscriptionID)
        .then()
        .assertThat()
        .statusCode(204);
  }

  @Test
  public void testToVerifyNotAllowingUpdateOfSecretInEventSubscription()
      throws JsonProcessingException {
    Response response =
        given()
            .contentType("application/json")
            .body(VALID_EVENT_SUBSCRIPTION)
            .post(EVENT_SUBSCRIPTIONS);

    String createdEventSubscriptionID = response.jsonPath().get("subscriptionID");

    JsonNode node = objectMapper.valueToTree(response.jsonPath().get());
    ((ObjectNode) node)
        .put(
            "secret",
            "OG1wOWFaRW1HTTF1Y2M4OUN0RlAtaU9JMjM5N25vMWtWd25rS2Vkc2ktZms0c01zaTJQOElZRVNQN29MYUkzcg==");

    given()
        .contentType("application/json")
        .body(objectMapper.writeValueAsString(node))
        .put(EVENT_SUBSCRIPTIONS + "/"+ createdEventSubscriptionID)
        .then()
        .assertThat()
        .statusCode(400)
        .body(jsonSchemaValidator("error"))
        .body(
            "errors.collect { it.message }",
            Matchers.hasItem(Matchers.containsString("Please omit the \"secret\" attribute.")));
  }

  @Test
  public void testToVerifyFailureIfSubscriptionIDInPathAndBodyDoNotMatchEventSubscription()
      throws JsonProcessingException {
    Response response =
        given()
            .contentType("application/json")
            .body(VALID_EVENT_SUBSCRIPTION)
            .post(EVENT_SUBSCRIPTIONS);

    String createdEventSubscriptionID = response.jsonPath().get("subscriptionID");

    JsonNode node = objectMapper.valueToTree(response.jsonPath().get());
    ((ObjectNode) node).put("subscriptionID", "190b766e-3d8a-43b2-962d-4df0b3284098");

    given()
        .contentType("application/json")
        .body(objectMapper.writeValueAsString(node))
        .put(EVENT_SUBSCRIPTIONS + "/"+ createdEventSubscriptionID)
        .then()
        .assertThat()
        .statusCode(400)
        .body(
            "errors.collect { it.message }",
            Matchers.hasItem(Matchers.containsString("Id in url does not match id in body")));
  }

  @Test
  public void testToUpdateEventSubscription() throws JsonProcessingException {

    UUID uuid = UUID.randomUUID();

    Response response =
        given()
            .contentType("application/json")
            .body(VALID_EVENT_SUBSCRIPTION)
            .post(EVENT_SUBSCRIPTIONS);

    String createdEventSubscriptionID = response.jsonPath().get("subscriptionID");

    JsonNode node = objectMapper.valueToTree(response.jsonPath().get());
    ((ObjectNode) node)
        .put("callbackUrl", "http://127.0.0.1:9092/v1/notification-endpoints/receive/" + uuid);

    given()
        .contentType("application/json")
        .body(objectMapper.writeValueAsString(node))
        .put(EVENT_SUBSCRIPTIONS +"/"+ createdEventSubscriptionID)
        .then()
        .assertThat()
        .statusCode(200)
        .body("callbackUrl", Matchers.containsString(uuid.toString()));
  }

  @Test
  public void testToUpdateSecretEventSubscription() {
    Response response =
        given()
            .contentType("application/json")
            .body(VALID_EVENT_SUBSCRIPTION)
            .post(EVENT_SUBSCRIPTIONS);

    String createdEventSubscriptionID = response.jsonPath().get("subscriptionID");

    given()
        .contentType("application/json")
        .body(
            "{\"secret\": \"MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDM2NTc4NjIzODk3NDY5MDgyNzM0OTg3MTIzNzg2NA==\"}")
        .put(EVENT_SUBSCRIPTIONS +"/"+ createdEventSubscriptionID + "/secret")
        .then()
        .assertThat()
        .statusCode(204);
  }
}
