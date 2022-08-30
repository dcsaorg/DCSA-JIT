package org.dcsa.jit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.dcsa.jit.persistence.entity.MessageRoutingRule;
import org.dcsa.jit.persistence.entity.OutboxMessage;
import org.dcsa.jit.persistence.repository.MessageRoutingRuleRepository;
import org.dcsa.jit.persistence.repository.OutboxMessageRepository;
import org.dcsa.jit.persistence.repository.TimestampNotificationDeadRepository;
import org.dcsa.jit.transferobjects.*;
import org.dcsa.jit.transferobjects.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Testcontainers
@ActiveProfiles("test")
@CamelSpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(stubs = "classpath:/stubs")
@ContextConfiguration(initializers = {TimestampRoutingServiceIT.Initializer.class})
class TimestampRoutingServiceIT {

  @Autowired MessageRoutingRuleRepository messageRoutingRuleRepository;

  @Autowired OutboxMessageRepository outboxMessageRepository;

  @Autowired TimestampNotificationDeadRepository timestampNotificationDeadRepository;

  @Autowired ObjectMapper objectMapper;

  @Autowired CamelContext camelContext;

  CountDownLatch countDownLatch = new CountDownLatch(1);

  private TimestampTO timestamp;

  @Container
  public static PostgreSQLContainer<?> postgresSQLContainer =
      new PostgreSQLContainer<>("postgres:10-alpine")
          .withCopyFileToContainer(
              MountableFile.forHostPath("../DCSA-Information-Model/datamodel/initdb.d"),
              "/docker-entrypoint-initdb.d")
          .withCopyFileToContainer(
              MountableFile.forHostPath("../DCSA-Information-Model/datamodel/testdata.d"),
              "/docker-entrypoint-initdb.d")
          .withCopyFileToContainer(
              MountableFile.forHostPath("../DCSA-Information-Model/datamodel/referencedata.d"),
              "/referencedata.d")
          .withCopyFileToContainer(
              MountableFile.forHostPath("../DCSA-Information-Model/datamodel/samples.d"),
              "/samples.d");

  static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
              "spring.datasource.url=jdbc:postgresql://"
                  + postgresSQLContainer.getHost()
                  + ":"
                  + postgresSQLContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
                  + "/dcsa_openapi?currentSchema=dcsa_im_v3_0",
              "spring.datasource.username=dcsa_db_owner",
              "spring.datasource.password=9c072fe8-c59c-11ea-b8d1-7b6577e9f3f5",
              "spring.datasource.driver-class-name=org.postgresql.Driver",
              "spring.jpa.hibernate.ddl-auto=none",
              "spring.jpa.database-platform: org.hibernate.dialect.PostgreSQL10Dialect",
              "camel.redelivery-delay=1000") // lower retry delay so the tests run quicker
          .applyTo(configurableApplicationContext.getEnvironment());
    }
  }

  @BeforeEach
  void init() {

    AddressTO addressTO =
        AddressTO.builder()
            .name("John")
            .street("Kronprincessegade")
            .streetNumber("54")
            .floor("5")
            .postCode("1306")
            .city("København")
            .stateRegion("N/A")
            .country("Denmark")
            .build();

    timestamp =
        TimestampTO.builder()
            .publisher(
                PartyTO.builder()
                    .partyName("Asseco Denmark")
                    .taxReference1("CVR-25645774")
                    .taxReference2("CVR-25645774")
                    .publicKey("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkFzaW")
                    .address(addressTO)
                    .identifyingCodes(
                        List.of(
                            IdentifyingCodeTO.builder()
                                .DCSAResponsibleAgencyCode(DCSAResponsibleAgencyCode.SMDG)
                                .partyCode("EMC")
                                .codeListName("Evergreen Marine Corporation")
                                .build()))
                    .nmftaCode("CSK")
                    .build())
            .vessel(TimestampVesselTO.builder().vesselIMONumber("9321483").build())
            .publisherRole(PublisherRole.CA)
            .vesselIMONumber("9321483")
            .UNLocationCode("SGSIN")
            .facilityTypeCode(FacilityTypeCodeOPR.BRTH)
            .eventClassifierCode(EventClassifierCode.EST)
            .operationsEventTypeCode(OperationsEventTypeCode.ARRI)
            .eventLocation(
                LocationTO.builder()
                    .locationName("Eiffel Tower")
                    .latitude("48.8585500")
                    .longitude("2.294492036")
                    .UNLocationCode("SGSIN")
                    .address(addressTO)
                    .facilityCode("PSABT")
                    .facilityCodeListProvider(FacilityCodeListProvider.SMDG)
                    .build())
            .vesselPosition(
                LocationTO.builder().latitude("48.8585500").longitude("2.294492036").build())
            .portCallPhaseTypeCode(null)
            .portCallPhaseTypeCode(PortCallPhaseTypeCode.INBD)
            .eventDateTime(OffsetDateTime.now())
            .carrierVoyageNumber("2103S")
            .carrierImportVoyageNumber("2103S")
            .carrierExportVoyageNumber("2103S")
            .carrierServiceCode("FE1")
            .transportCallSequenceNumber(2)
            .remark("Port closed due to strike")
            .delayReasonCode("WEA")
            .build();
  }

  @Test
  void after_valid_timestamp_insert_should_notify_successfully_on_valid_credentials_test()
      throws Exception {
    // mock all endpoints, add an advice on the routes you want to track
    AdviceWith.adviceWith(
        camelContext, "outbox-message-poll", AdviceWithRouteBuilder::mockEndpoints);
    AdviceWith.adviceWith(camelContext, "emit-msg", AdviceWithRouteBuilder::mockEndpoints);
    // get the mock endpoints so you can test against them
    MockEndpoint emitMessageRoute =
        camelContext.getEndpoint("mock:direct:emit-message", MockEndpoint.class);
    MockEndpoint timestampNotifyRoute =
        camelContext.getEndpoint("mock:direct:timestamp-notification", MockEndpoint.class);
    // set expected message count
    emitMessageRoute.setExpectedMessageCount(1);
    timestampNotifyRoute.setExpectedMessageCount(1);

    // create a message route with valid mocks for success
    MessageRoutingRule messageRoutingRule =
        MessageRoutingRule.builder()
            .apiUrl("http://localhost:8080/v1/timestamps")
            .loginType(MessageRoutingRule.LoginType.OIDC)
            .loginInformation(
                MessageRoutingRule.LoginInformation.builder()
                    .clientID("dcsa-api")
                    .clientSecret("X32D3a5FoiDRLzXzJMCFW5Q7pMcbZh6o")
                    .tokenURL("http://localhost:8080/auth/token")
                    .build())
            .build();

    MessageRoutingRule savedMessageRoutingRule =
        messageRoutingRuleRepository.save(messageRoutingRule);

    OutboxMessage outboxMessage =
        OutboxMessage.builder()
            .messageRoutingRule(savedMessageRoutingRule)
            .payload(objectMapper.writeValueAsString(timestamp))
            .build();

    // insert into outbox_message to trigger the routes polling
    outboxMessageRepository.save(outboxMessage);

    // asserts
    assertNotNull(camelContext.hasEndpoint("mock:direct:emit-message"));
    assertNotNull(camelContext.hasEndpoint("mock:direct:timestamp-notification"));

    emitMessageRoute.expectedMessageCount(1);
    emitMessageRoute.assertIsSatisfied();
    timestampNotifyRoute.assertIsSatisfied();
    timestampNotifyRoute.expectedBodiesReceived(
        "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ4cHZaUHdpbFhaYmE5QUhYRmprclctbC14T3AxY1ZEYmVnNzcyMVMwS1VzIn0.eyJleHAiOjE2NjEzNTQwODEsImlhdCI6MTY2MTM1Mzc4MSwianRpIjoiM2Y2Zjk5NmYtNDVkYS00ZGU4LThkZDctYWZjYjVlMjM1NjkxIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy9kY3NhIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImZkMGE4ZTRmLWNhN2ItNDRjNy04NWI4LWFiMzQ2OWVjMWVmNSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImRjc2EtYXBpIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1kY3NhIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJjbGllbnRIb3N0IjoiMTcyLjE3LjAuMSIsImNsaWVudElkIjoiZGNzYS1hcGkiLCJjb2duaXRvOmdyb3VwcyI6WyJtc2MiLCJvbmVsaW5lIiwiZXZlcmdyZWVubWFyaW5lIiwiaHl1bmRhaSIsIm1hZXJzayIsImh2Y2MtaGFtYnVyZyIsImNtYWNnbSIsInBzYSIsImhhcGFnbGxveWQiLCJ3YXJ0c2lsYSJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzZXJ2aWNlLWFjY291bnQtZGNzYS1hcGkiLCJjbGllbnRBZGRyZXNzIjoiMTcyLjE3LjAuMSJ9.b7k6mOFoiTBq-mmRX2XmtMldcXVKiRqO_xERyQfibEm9dp_p5RT1YFzsqvPxBWUbMKNgq8JS_awkWeB5GQ2n44olaLVYz6rB6wW8pw7mEa0vJsuerhMnu8t4lhQfBKlfeS966505ur4mTaSYcXuAgzxvqgM36ldl6ZkFl5glMhN70lMZpzL9JZRJSlSepIN1HHX3sLCwXTCRBK210DsZSIZu4dNbObqdtHz5DWJC8Lw2bhvFUHFgkH2rDvtT5D15yk78BIj1BOS9072jdQN2kFxBo0MTfLONERro_yH9MBgZyL6l4YF9Uziyi4lwvP0LdN0hIyw_i-UUL9QI3hfniw");
  }

  @Test
  void
      after_valid_timestamp_insert_notify_should_fail_for_invalid_credentials_and_update_dead_queue_test()
          throws Exception {
    // mock all endpoints, add an advice on the routes you want to track
    AdviceWith.adviceWith(
        camelContext, "outbox-message-poll", AdviceWithRouteBuilder::mockEndpoints);
    AdviceWith.adviceWith(camelContext, "emit-msg", AdviceWithRouteBuilder::mockEndpoints);
    // get the mock endpoints so you can test against them
    MockEndpoint emitMessageRoute =
        camelContext.getEndpoint("mock:direct:emit-message", MockEndpoint.class);
    MockEndpoint timestampNotifyRoute =
        camelContext.getEndpoint("mock:direct:timestamp-notification", MockEndpoint.class);
    // set expected message count
    emitMessageRoute.setExpectedMessageCount(1);
    timestampNotifyRoute.setExpectedMessageCount(1);

    // create a message route with valid mocks for success
    MessageRoutingRule messageRoutingRule =
        MessageRoutingRule.builder()
            .apiUrl("http://localhost:8080/v1/timestamps-failure")
            .loginType(MessageRoutingRule.LoginType.OIDC)
            .loginInformation(
                MessageRoutingRule.LoginInformation.builder()
                    .clientID("dcsa-api")
                    .clientSecret("X32D3a5FoiDRLzXzJMCFW5Q7pMcbZh6o")
                    .tokenURL("http://localhost:8080/auth/token-failure")
                    .build())
            .build();

    MessageRoutingRule savedMessageRoutingRule =
        messageRoutingRuleRepository.save(messageRoutingRule);

    OutboxMessage outboxMessage =
        OutboxMessage.builder()
            .messageRoutingRule(savedMessageRoutingRule)
            .payload(objectMapper.writeValueAsString(timestamp))
            .build();

    // insert into outbox_message to trigger the routes polling
    outboxMessageRepository.save(outboxMessage);

    // asserts
    assertNotNull(camelContext.hasEndpoint("mock:direct:emit-message"));
    assertNotNull(camelContext.hasEndpoint("mock:direct:timestamp-notification"));

    emitMessageRoute.expectedMessageCount(1);
    emitMessageRoute.assertIsSatisfied();
    timestampNotifyRoute.expectedBodiesReceived("Bearer <token>");
    timestampNotifyRoute.assertIsSatisfied();

    countDownLatch.await(20000, TimeUnit.MILLISECONDS);

    assertEquals(1, timestampNotificationDeadRepository.findAll().size());
  }
}
