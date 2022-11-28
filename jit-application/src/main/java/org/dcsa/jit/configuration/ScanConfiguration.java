package org.dcsa.jit.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("org.dcsa")
@EntityScan("org.dcsa")
@EnableJpaRepositories("org.dcsa")
public class ScanConfiguration {
}
