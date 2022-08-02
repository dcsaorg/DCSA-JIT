package org.dcsa.jit.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Profile({"dev", "test", "!prod"})
@Slf4j
@EnableWebSecurity
public class SecurityConfig {

  @Autowired private Environment environment;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.cors()
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
        .httpBasic()
        .disable()
        .csrf()
        .disable()
        .build();
  }

  @EventListener(ApplicationStartedEvent.class)
  void securityLogConfiguration() {
    log.info(
        "You are currently running : {} : security is disabled for this profile. "
            + "Please use `prod` profile to activate security.",
        environment.getActiveProfiles());
  }
}
