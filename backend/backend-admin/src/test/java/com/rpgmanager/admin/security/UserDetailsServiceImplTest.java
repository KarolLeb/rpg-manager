package com.rpgmanager.admin.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.rpgmanager.admin.user.domain.model.UserDomain;
import com.rpgmanager.admin.user.domain.repository.UserRepositoryPort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

  @Mock private UserRepositoryPort userRepository;

  private UserDetailsServiceImpl userDetailsService;

  @BeforeEach
  void setUp() {
    userDetailsService = new UserDetailsServiceImpl(userRepository);
  }

  @Test
  void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
    UserDomain user = new UserDomain();
    user.setUsername("admin");
    user.setPassword("password");
    user.setRole(UserDomain.Role.ADMIN);

    given(userRepository.findByUsername("admin")).willReturn(Optional.of(user));

    UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

    assertThat(userDetails.getUsername()).isEqualTo("admin");
    assertThat(userDetails.getAuthorities()).hasSize(1);
    assertThat(userDetails.getAuthorities().iterator().next().getAuthority())
        .isEqualTo("ROLE_ADMIN");
  }

  @Test
  void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
    given(userRepository.findByUsername("nonexistent")).willReturn(Optional.empty());

    assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nonexistent"))
        .isInstanceOf(UsernameNotFoundException.class);
  }
}
