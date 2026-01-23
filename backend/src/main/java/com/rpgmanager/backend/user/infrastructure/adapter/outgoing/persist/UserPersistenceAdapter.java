package com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist;

import com.rpgmanager.backend.user.domain.model.UserDomain;
import com.rpgmanager.backend.user.domain.repository.UserRepositoryPort;
import com.rpgmanager.backend.user.infrastructure.mapper.UserPersistenceMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

  private final JpaUserRepository jpaUserRepository;
  private final UserPersistenceMapper userPersistenceMapper;

  @Override
  public java.util.List<UserDomain> findAll() {
    return jpaUserRepository.findAll().stream().map(userPersistenceMapper::toDomain).toList();
  }

  @Override
  public Optional<UserDomain> findById(Long id) {
    return jpaUserRepository.findById(id).map(userPersistenceMapper::toDomain);
  }

  @Override
  public Optional<UserDomain> findByUsername(String username) {
    return jpaUserRepository.findByUsername(username).map(userPersistenceMapper::toDomain);
  }

  @Override
  public UserDomain save(UserDomain user) {
    UserEntity entity = userPersistenceMapper.toEntity(user);
    UserEntity savedEntity = jpaUserRepository.save(entity);
    return userPersistenceMapper.toDomain(savedEntity);
  }

  @Override
  public boolean existsById(Long id) {
    return jpaUserRepository.existsById(id);
  }
}
