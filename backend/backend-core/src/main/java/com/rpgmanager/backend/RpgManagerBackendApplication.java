package com.rpgmanager.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

/** Main entry point for the RPG Manager Backend application. */
@SpringBootApplication
@EnableCaching
@EnableFeignClients
public class RpgManagerBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(RpgManagerBackendApplication.class, args);
  }
}
