package com.rpgmanager.auth.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.rpgmanager.auth.user.domain.model.UserDomain;
import com.rpgmanager.auth.user.domain.repository.UserRepositoryPort;
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
    user.setUsername("testuser");
    user.setPassword("password");
    user.setRole(UserDomain.Role.PLAYER);

    given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));

    UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

    assertThat(userDetails.getUsername()).isEqualTo("testuser");
    assertThat(userDetails.getAuthorities()).hasSize(1);
    assertThat(userDetails.getAuthorities().iterator().next().getAuthority())
        .isEqualTo("ROLE_PLAYER");
  }

  @Test
  void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
    given(userRepository.findByUsername("nonexistent")).willReturn(Optional.empty());

    assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nonexistent"))
        .isInstanceOf(UsernameNotFoundException.class);
  }
}
