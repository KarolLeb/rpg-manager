package com.rpgmanager.auth.user.infrastructure.adapter.outgoing.persist;

import com.rpgmanager.auth.user.domain.model.UserDomain;
import com.rpgmanager.auth.user.domain.repository.UserRepositoryPort;
import com.rpgmanager.auth.user.infrastructure.mapper.UserPersistenceMapper;
import java.util.Optional;
import org.springframework.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Adapter implementation for User persistence. */
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
  public Optional<UserDomain> findById(@NonNull Long id) {
    return jpaUserRepository.findById(id).map(userPersistenceMapper::toDomain);
  }

  @Override
  public Optional<UserDomain> findByUsername(@NonNull String username) {
    return jpaUserRepository.findByUsername(username).map(userPersistenceMapper::toDomain);
  }

  @Override
  public UserDomain save(@NonNull UserDomain user) {
    UserEntity entity = userPersistenceMapper.toEntity(user);
    UserEntity savedEntity = jpaUserRepository.save(entity);
    return userPersistenceMapper.toDomain(savedEntity);
  }

  @Override
  public boolean existsById(@NonNull Long id) {
    return jpaUserRepository.existsById(id);
  }
}
