package com.rpgmanager.backend.config;

import java.security.SecureRandom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration class for application-wide beans. */
@Configuration
public class AppConfig {

  @Bean
  public SecureRandom secureRandom() {
    return new SecureRandom();
  }
}
