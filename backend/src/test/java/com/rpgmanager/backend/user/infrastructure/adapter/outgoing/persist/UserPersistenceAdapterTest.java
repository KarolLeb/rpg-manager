package com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.rpgmanager.backend.user.domain.model.UserDomain;
import com.rpgmanager.backend.user.infrastructure.mapper.UserPersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserPersistenceAdapterTest {

  @Mock private JpaUserRepository jpaUserRepository;
  @Mock private UserPersistenceMapper userPersistenceMapper;

  @InjectMocks private UserPersistenceAdapter adapter;

  @Test
  void findAll_shouldReturnList() {
    UserEntity entity = Instancio.create(UserEntity.class);
    UserDomain domain = Instancio.create(UserDomain.class);

    when(jpaUserRepository.findAll()).thenReturn(List.of(entity));
    when(userPersistenceMapper.toDomain(entity)).thenReturn(domain);

    List<UserDomain> result = adapter.findAll();

    assertThat(result).hasSize(1);
    verify(jpaUserRepository).findAll();
  }

  @Test
  void findById_shouldReturnDomain() {
    UserEntity entity = Instancio.create(UserEntity.class);
    UserDomain domain = Instancio.create(UserDomain.class);

    when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(userPersistenceMapper.toDomain(entity)).thenReturn(domain);

    Optional<UserDomain> result = adapter.findById(1L);

    assertThat(result).isPresent();
    verify(jpaUserRepository).findById(1L);
  }

  @Test
  void findByUsername_shouldReturnDomain() {
    UserEntity entity = Instancio.create(UserEntity.class);
    UserDomain domain = Instancio.create(UserDomain.class);

    when(jpaUserRepository.findByUsername("user")).thenReturn(Optional.of(entity));
    when(userPersistenceMapper.toDomain(entity)).thenReturn(domain);

    Optional<UserDomain> result = adapter.findByUsername("user");

    assertThat(result).isPresent();
  }

  @Test
  void save_shouldSaveAndReturnDomain() {
    UserDomain domain = Instancio.create(UserDomain.class);
    UserEntity entity = Instancio.create(UserEntity.class);

    when(userPersistenceMapper.toEntity(domain)).thenReturn(entity);
    when(jpaUserRepository.save(entity)).thenReturn(entity);
    when(userPersistenceMapper.toDomain(entity)).thenReturn(domain);

    UserDomain result = adapter.save(domain);

    assertThat(result).isNotNull();
    verify(jpaUserRepository).save(entity);
  }

  @Test
  void existsById_shouldReturnBoolean() {
    when(jpaUserRepository.existsById(1L)).thenReturn(true);
    assertThat(adapter.existsById(1L)).isTrue();
  }
}
