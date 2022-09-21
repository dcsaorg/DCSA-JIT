package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.enums.PublisherRole;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "message_routing_rule")
public class MessageRoutingRule {
  public enum LoginType {
    OIDC
  }

  public record LoginInformation( // Login credentials
    String clientID,
    String clientSecret,
    String tokenURL
  ) {
    @Builder(toBuilder = true) // workaround for intellij issue
    public LoginInformation {}
  }

  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "api_url", nullable = false)
  private String apiUrl; // destination url

  @Enumerated(EnumType.STRING)
  @Column(name = "login_type", length = 8, nullable = false)
  private LoginType loginType;

  @Type(type = "com.vladmihalcea.hibernate.type.json.JsonStringType")
  @Column(name = "login_information", nullable = false, columnDefinition = "TEXT")
  private LoginInformation loginInformation;

  @Column(name = "vessel_imo_number")
  private String vesselIMONumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "publisher_role")
  private PublisherRole publisherRole;
}
