package com.rpgmanager.backend;

import com.rpgmanager.backend.config.TestContainersConfig;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestContainersConfig.class)
@Transactional
public abstract class BaseIntegrationTest {

  @Autowired private RedisTemplate<Object, Object> redisTemplate;

  @AfterEach
  void tearDown() {
    if (redisTemplate.getConnectionFactory() != null) {
      redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
  }
}
