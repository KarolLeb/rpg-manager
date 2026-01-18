package com.rpgmanager.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = { RedisRepositoriesAutoConfiguration.class })
@EnableCaching
@EnableFeignClients
public class RpgManagerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RpgManagerBackendApplication.class, args);
	}

}