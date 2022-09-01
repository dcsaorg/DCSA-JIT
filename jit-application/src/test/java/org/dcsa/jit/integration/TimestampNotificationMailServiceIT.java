package org.dcsa.jit.integration;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.restassured.RestAssured;
import lombok.SneakyThrows;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.MountableFile;

import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Testcontainers
@ActiveProfiles("test")
@CamelSpringBootTest
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {TimestampNotificationMailServiceIT.Initializer.class})
public class TimestampNotificationMailServiceIT {
  public static final String VALID_TIMESTAMP_1_2 =
    loadFileAsString("TimestampSample_v1-2.json");

  @Container
  public static PostgreSQLContainer<?> postgresSQLContainer =
    new PostgreSQLContainer<>("postgres:10-alpine")
      .withCopyFileToContainer(
        MountableFile.forHostPath("../DCSA-Information-Model/datamodel/initdb.d"), "/docker-entrypoint-initdb.d")
      .withCopyFileToContainer(
        MountableFile.forHostPath("../DCSA-Information-Model/datamodel/testdata.d"), "/docker-entrypoint-initdb.d")
      .withCopyFileToContainer(
        MountableFile.forHostPath("../DCSA-Information-Model/datamodel/referencedata.d"), "/referencedata.d")
      .withCopyFileToContainer(
        MountableFile.forHostPath("../DCSA-Information-Model/datamodel/samples.d"), "/samples.d");

  @RegisterExtension
  static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
    .withConfiguration(GreenMailConfiguration.aConfig().withUser("dcsa", "springboot"))
    .withPerMethodLifecycle(false);

  static class Initializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
          "spring.datasource.url=jdbc:postgresql://" + postgresSQLContainer.getHost() + ":"
            + postgresSQLContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
            + "/dcsa_openapi?currentSchema=dcsa_im_v3_0",
          "spring.datasource.username=dcsa_db_owner",
          "spring.datasource.password=9c072fe8-c59c-11ea-b8d1-7b6577e9f3f5",
          "spring.datasource.driver-class-name=org.postgresql.Driver",
          "spring.jpa.hibernate.ddl-auto=none",
          "spring.jpa.database-platform: org.hibernate.dialect.PostgreSQL10Dialect",
          "spring.mail.host=127.0.0.1",
          "spring.mail.port=3025",
          "spring.mail.username=dcsa",
          "spring.mail.password=springboot",
          "spring.mail.protocol=smtp",
          "spring.mail.test-connection=false",
          "spring.mail.properties.mail.smtp.starttls.enable=false",
          "spring.mail.properties.mail.smtp.starttls.required=false",
          "dcsa.email.templates.timestampReceived.to=dcsatest@dcsa.org.invalid",
          "dcsa.email.templates.timestampReceived.subject=Subject line",
          "dcsa.email.templates.timestampReceived.body=Body content"
        )
        .applyTo(configurableApplicationContext.getEnvironment());
    }
  }

  @LocalServerPort
  private int serverPort;

  @Test
  @DisplayName("Test when a timestamp is received an email is sent")
  public void testOnTimestampEmailSent() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = serverPort;
    given()
      .contentType("application/json")
      .body(VALID_TIMESTAMP_1_2)
      .post("/v1/timestamps")
      .then()
      .assertThat()
      .statusCode(204);

    Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
      MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
      assertEquals(1, receivedMessages.length);

      MimeMessage receivedMessage = receivedMessages[0];
      assertEquals("Subject line", receivedMessage.getSubject());
      assertEquals("Body content", GreenMailUtil.getBody(receivedMessage));
      assertEquals(1, receivedMessage.getAllRecipients().length);
      assertEquals("dcsatest@dcsa.org.invalid", receivedMessage.getAllRecipients()[0].toString());
    });
  }

  @SneakyThrows
  public static String loadFileAsString(String resource) {
    try (Reader dataInputStream = new BufferedReader(new InputStreamReader(openStream(resource), StandardCharsets.UTF_8))) {
      StringBuilder stringBuilder = new StringBuilder();
      char[] buffer = new char[4096];
      int read;
      while ((read = dataInputStream.read(buffer)) > 0) {
        stringBuilder.append(buffer, 0, read);
      }
      return stringBuilder.toString().trim();
    }
  }

  private static InputStream openStream(String resource) throws IOException {
    URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
    if (url == null) {
      throw new IllegalStateException("Cannot find json file " + resource);
    }
    return url.openStream();
  }
}
