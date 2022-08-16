package org.dcsa.jit.persistence.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "message_routing_rule")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
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

  @Type(type = "jsonb")
  @Column(name = "login_information", nullable = false, columnDefinition = "jsonb")
  private LoginInformation loginInformation;

  @Column(name = "vessel_imo_number")
  private String vesselIMONumber;
}
