package com.rpgmanager.auth.user.infrastructure.adapter.outgoing.persist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.rpgmanager.auth.user.domain.model.UserDomain;
import com.rpgmanager.auth.user.infrastructure.mapper.UserPersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserPersistenceAdapterTest {

  @Mock private JpaUserRepository jpaUserRepository;
  @Mock private UserPersistenceMapper userPersistenceMapper;

  private UserPersistenceAdapter userPersistenceAdapter;

  @BeforeEach
  void setUp() {
    userPersistenceAdapter = new UserPersistenceAdapter(jpaUserRepository, userPersistenceMapper);
  }

  @Test
  void findAll_shouldReturnList() {
    UserEntity entity = new UserEntity();
    UserDomain domain = new UserDomain();
    given(jpaUserRepository.findAll()).willReturn(List.of(entity));
    given(userPersistenceMapper.toDomain(entity)).willReturn(domain);

    List<UserDomain> result = userPersistenceAdapter.findAll();

    assertThat(result).hasSize(1);
  }

  @Test
  void findById_shouldReturnUser_whenExists() {
    UserEntity entity = new UserEntity();
    UserDomain domain = new UserDomain();
    given(jpaUserRepository.findById(1L)).willReturn(Optional.of(entity));
    given(userPersistenceMapper.toDomain(entity)).willReturn(domain);

    Optional<UserDomain> result = userPersistenceAdapter.findById(1L);

    assertThat(result).isPresent();
  }

  @Test
  void findByUsername_shouldReturnUser_whenExists() {
    UserEntity entity = new UserEntity();
    UserDomain domain = new UserDomain();
    given(jpaUserRepository.findByUsername("testuser")).willReturn(Optional.of(entity));
    given(userPersistenceMapper.toDomain(entity)).willReturn(domain);

    Optional<UserDomain> result = userPersistenceAdapter.findByUsername("testuser");

    assertThat(result).isPresent();
  }

  @Test
  void save_shouldReturnSavedUser() {
    UserEntity entity = new UserEntity();
    UserDomain domain = new UserDomain();
    given(userPersistenceMapper.toEntity(domain)).willReturn(entity);
    given(jpaUserRepository.save(entity)).willReturn(entity);
    given(userPersistenceMapper.toDomain(entity)).willReturn(domain);

    UserDomain result = userPersistenceAdapter.save(domain);

    assertThat(result).isEqualTo(domain);
    verify(jpaUserRepository).save(entity);
  }

  @Test
  void existsById_shouldReturnTrue_whenExists() {
    given(jpaUserRepository.existsById(1L)).willReturn(true);

    boolean result = userPersistenceAdapter.existsById(1L);

    assertThat(result).isTrue();
    verify(jpaUserRepository).existsById(1L);
  }
}
