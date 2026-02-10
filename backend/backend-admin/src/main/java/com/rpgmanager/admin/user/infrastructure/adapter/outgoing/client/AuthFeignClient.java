package com.rpgmanager.admin.user.infrastructure.adapter.outgoing.client;

import com.rpgmanager.admin.user.domain.model.UserDomain;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", url = "${app.auth-service.url:http://localhost:8081}")
public interface AuthFeignClient {

  @GetMapping("/api/users/{id}")
  UserDomain getUserById(@PathVariable("id") Long id);

  @GetMapping("/api/users")
  UserDomain getUserByUsername(@RequestParam("username") String username);

  @GetMapping("/api/users/all")
  List<UserDomain> getAllUsers();
}
