package org.dcsa.jit.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Set;

/** Configures our application with Spring Security to restrict access to our API endpoints. */
@Profile("prod")
@Slf4j
@EnableWebSecurity
public class SecurityConfigProd {

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
  private String issuer;

  @Value("${dcsa.securityConfig.jwt.audience:NONE}")
  private String audience;

  @Value("${dcsa.securityConfig.jwt.claim.name:}")
  private String claimName;

  @Value("${dcsa.securityConfig.jwt.claim.value:}")
  private String claimValue;

  @Value("${dcsa.securityConfig.jwt.claim.shape:STRING}")
  private ClaimShape claimShape;

  @Value("${dcsa.securityConfig.csrf.enabled:false}")
  private boolean csrfEnabled;

  @Value("${dcsa.securityConfig.receiveNotificationEndpoint:NONE}")
  private String receiveNotificationEndpoint;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    /*
    This is where we configure the security required for our endpoints and setup our app to serve as
    an OAuth2 Resource Server, using JWT validation.
    */

    ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authz =
        http.cors()
            .configurationSource(
                request -> {
                  CorsConfiguration corsConfiguration = new CorsConfiguration();
                  corsConfiguration.addAllowedOrigin("*");
                  corsConfiguration.addAllowedHeader("*");
                  corsConfiguration.addAllowedMethod("*");
                  UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                  source.registerCorsConfiguration("/**", corsConfiguration);
                  return corsConfiguration;
                })
            .and()
            .authorizeRequests();

    authz.antMatchers(HttpMethod.GET, "/actuator/health").permitAll();

    String endpoint = null;
    log.info("Security: auth enabled (dcsa.securityConfig.auth.enabled)");
    log.info("Security: JWT with Issuer URI: " + issuer);

    if (!receiveNotificationEndpoint.equals("NONE")) {
      endpoint = receiveNotificationEndpoint.replaceAll("/++$", "") + "/receive/*";
      authz
          .antMatchers(HttpMethod.POST, endpoint)
          .permitAll()
          .antMatchers(HttpMethod.HEAD, endpoint)
          .permitAll();

      log.info("Security: receive endpoint \"" + endpoint + "\"");
    } else {
      log.info("Security: No receive receive endpoint");
    }

    log.info("Security: JWT issuer-uri: {}", issuer);
    log.info("Security: JWT audience required: " + audience);
    if (!claimName.equals("") && !claimValue.equals("")) {
      String values = String.join(", ", claimValue);
      log.info(
          "Security: JWT claims must have claim \"{}\" (shape: {}) containing one of: {}",
          claimName,
          claimShape,
          values);
      log.info(
          "Security: JWT claims can be controlled via dcsa.securityConfig.jwt.claim.{name,value,shape}");
    } else {
      log.info(
          "Security: No claim requirements for JWT tokens (dcsa.securityConfig.jwt.claim.{name,value,shape})");
    }

    authz.anyRequest().authenticated();

    if (StringUtils.isNotEmpty(issuer)) {
      http.oauth2ResourceServer(
          oauth2 -> oauth2.jwt().jwtAuthenticationConverter(new JwtAuthenticationConverter()));
    }

    if (endpoint != null) {
      String finalEndpoint = endpoint;
      http.csrf()
          .requireCsrfProtectionMatcher(request -> !request.getRequestURI().equals(finalEndpoint));
    }
    if (csrfEnabled) {
      log.info("Security: CSRF tokens required (dcsa.securityConfig.csrf.enabled)");
    } else {
      http.csrf().disable();
      log.info("Security: CSRF tokens disabled (dcsa.securityConfig.csrf.enabled)");
    }

    return http.build();
  }

  @Bean
  @ConditionalOnExpression(
      "T(org.apache.commons.lang3.StringUtils).isNotEmpty('${spring.security.oauth2.resourceserver.jwt.issuer-uri:}')")
  JwtDecoder jwtDecoder() {
    /*
    By default, Spring Security does not validate the "aud" claim of the token, to ensure that this token is
    indeed intended for our app. Adding our own validator is easy to do:
    */

    NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuer);

    OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
    OAuth2TokenValidator<Jwt> jwtValidator =
        ClaimsOneOfValueValidator.of(claimName, Set.of(claimValue), claimShape);
    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
    OAuth2TokenValidator<Jwt> withAudience =
        new DelegatingOAuth2TokenValidator<>(
            withIssuer, audienceValidator, jwtValidator, new JwtTimestampValidator());

    jwtDecoder.setJwtValidator(withAudience);
    return jwtDecoder;
  }

  @EventListener(ApplicationStartedEvent.class)
  @ConditionalOnExpression(
      "T(org.apache.commons.lang3.StringUtils).isEmpty('${spring.security.oauth2.resourceserver.jwt.issuer-uri:}')")
  void securityLogConfiguration() {
    log.info(
        "JWT is disabled as `spring.security.oauth2.resourceserver.jwt.issuer-uri` is missing.");
  }
}
