package com.rpgmanager.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configuration properties for security. */
@Data
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
  private JwtProperties jwt = new JwtProperties();

  /** JWT properties. */
  @Data
  public static class JwtProperties {
    private String secret;
    private long expiration;
  }
}
