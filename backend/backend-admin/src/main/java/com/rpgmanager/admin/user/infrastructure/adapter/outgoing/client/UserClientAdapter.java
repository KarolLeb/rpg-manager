package com.rpgmanager.admin.user.infrastructure.adapter.outgoing.client;

import com.rpgmanager.admin.user.domain.model.UserDomain;
import com.rpgmanager.admin.user.domain.repository.UserRepositoryPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserClientAdapter implements UserRepositoryPort {

  private final AuthFeignClient authFeignClient;

  @Override
  public List<UserDomain> findAll() {
    try {
      return authFeignClient.getAllUsers();
    } catch (Exception e) {
      return List.of();
    }
  }

  @Override
  public Optional<UserDomain> findByUsername(String username) {
    try {
      return Optional.ofNullable(authFeignClient.getUserByUsername(username));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public UserDomain save(UserDomain user) {
    throw new UnsupportedOperationException("Admin service cannot save users directly");
  }
}
