package com.rpgmanager.auth.user.infrastructure.adapter.outgoing.persist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.rpgmanager.auth.user.domain.model.UserDomain;
import com.rpgmanager.auth.user.infrastructure.mapper.UserPersistenceMapper;
import java.util.List;
import java.util.Optional;
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
    UserEntity entity = new UserEntity();
    UserDomain domain = new UserDomain();
    given(jpaUserRepository.findAll()).willReturn(List.of(entity));
    given(userPersistenceMapper.toDomain(entity)).willReturn(domain);

    List<UserDomain> result = adapter.findAll();

    assertThat(result).hasSize(1).contains(domain);
  }

  @Test
  void findById_shouldReturnDomain() {
    UserEntity entity = new UserEntity();
    UserDomain domain = new UserDomain();
    given(jpaUserRepository.findById(1L)).willReturn(Optional.of(entity));
    given(userPersistenceMapper.toDomain(entity)).willReturn(domain);

    Optional<UserDomain> result = adapter.findById(1L);

    assertThat(result).isPresent().contains(domain);
  }

  @Test
  void findByUsername_shouldReturnDomain() {
    UserEntity entity = new UserEntity();
    UserDomain domain = new UserDomain();
    given(jpaUserRepository.findByUsername("test")).willReturn(Optional.of(entity));
    given(userPersistenceMapper.toDomain(entity)).willReturn(domain);

    Optional<UserDomain> result = adapter.findByUsername("test");

    assertThat(result).isPresent().contains(domain);
  }

  @Test
  void save_shouldSaveAndReturnDomain() {
    UserDomain domain = new UserDomain();
    UserEntity entity = new UserEntity();
    UserEntity savedEntity = new UserEntity();
    UserDomain savedDomain = new UserDomain();

    given(userPersistenceMapper.toEntity(domain)).willReturn(entity);
    given(jpaUserRepository.save(entity)).willReturn(savedEntity);
    given(userPersistenceMapper.toDomain(savedEntity)).willReturn(savedDomain);

    UserDomain result = adapter.save(domain);

    assertThat(result).isEqualTo(savedDomain);
    verify(jpaUserRepository).save(entity);
  }

  @Test
  void existsById_shouldReturnTrue_whenExists() {
    given(jpaUserRepository.existsById(1L)).willReturn(true);
    assertThat(adapter.existsById(1L)).isTrue();
  }
}
