package com.rpgmanager.auth.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configuration properties for security settings. */
@Configuration
@ConfigurationProperties(prefix = "rpg.security")
@Getter
@Setter
public class SecurityProperties {

  /** List of allowed origins for CORS and browser navigation filtering. */
  private List<String> allowedOrigins;
}
