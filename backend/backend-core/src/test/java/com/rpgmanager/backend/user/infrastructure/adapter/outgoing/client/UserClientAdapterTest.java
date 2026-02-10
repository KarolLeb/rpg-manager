package com.rpgmanager.backend.user.infrastructure.adapter.outgoing.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.rpgmanager.backend.user.domain.model.UserDomain;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserClientAdapterTest {

  @Mock private AuthFeignClient authFeignClient;

  private UserClientAdapter userClientAdapter;

  @BeforeEach
  void setUp() {
    userClientAdapter = new UserClientAdapter(authFeignClient);
  }

  @Test
  void shouldFindAllUsers() {
    UserDomain user = UserDomain.builder().id(1L).username("test").build();
    given(authFeignClient.getAllUsers()).willReturn(List.of(user));

    List<UserDomain> result = userClientAdapter.findAll();

    assertThat(result).containsExactly(user);
  }

  @Test
  void shouldReturnEmptyListOnFindAllError() {
    given(authFeignClient.getAllUsers()).willThrow(new RuntimeException("API Error"));

    List<UserDomain> result = userClientAdapter.findAll();

    assertThat(result).isEmpty();
  }

  @Test
  void shouldFindUserById() {
    UserDomain user = UserDomain.builder().id(1L).username("test").build();
    given(authFeignClient.getUserById(1L)).willReturn(user);

    Optional<UserDomain> result = userClientAdapter.findById(1L);

    assertThat(result).contains(user);
  }

  @Test
  void shouldReturnEmptyOnFindByIdError() {
    given(authFeignClient.getUserById(1L)).willThrow(new RuntimeException("API Error"));

    Optional<UserDomain> result = userClientAdapter.findById(1L);

    assertThat(result).isEmpty();
  }

  @Test
  void shouldFindUserByUsername() {
    UserDomain user = UserDomain.builder().id(1L).username("test").build();
    given(authFeignClient.getUserByUsername("test")).willReturn(user);

    Optional<UserDomain> result = userClientAdapter.findByUsername("test");

    assertThat(result).contains(user);
  }

  @Test
  void shouldReturnEmptyOnFindByUsernameError() {
    given(authFeignClient.getUserByUsername("test")).willThrow(new RuntimeException("API Error"));

    Optional<UserDomain> result = userClientAdapter.findByUsername("test");

    assertThat(result).isEmpty();
  }
}
