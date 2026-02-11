package com.rpgmanager.admin.user.infrastructure.adapter.outgoing.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.rpgmanager.admin.user.domain.model.UserDomain;
import java.util.List;
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
  void findAll_shouldReturnList() {
    UserDomain user = new UserDomain();
    given(authFeignClient.getAllUsers()).willReturn(List.of(user));

    List<UserDomain> result = userClientAdapter.findAll();

    assertThat(result).hasSize(1);
  }

  @Test
  void findAll_shouldReturnEmptyList_whenExceptionOccurs() {
    given(authFeignClient.getAllUsers()).willThrow(new RuntimeException("API error"));

    List<UserDomain> result = userClientAdapter.findAll();

    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_shouldReturnUser_whenExists() {
    UserDomain user = new UserDomain();
    given(authFeignClient.getUserByUsername("admin")).willReturn(user);

    assertThat(userClientAdapter.findByUsername("admin")).isPresent();
  }

  @Test
  void findByUsername_shouldReturnEmpty_whenExceptionOccurs() {
    given(authFeignClient.getUserByUsername("admin")).willThrow(new RuntimeException("API error"));

    assertThat(userClientAdapter.findByUsername("admin")).isEmpty();
  }

  @Test
  void save_shouldThrowException() {
    UserDomain user = new UserDomain();
    assertThatThrownBy(() -> userClientAdapter.save(user))
        .isInstanceOf(UnsupportedOperationException.class);
  }
}
