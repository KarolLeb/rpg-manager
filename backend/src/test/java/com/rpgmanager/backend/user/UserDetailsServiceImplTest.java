package com.rpgmanager.backend.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.rpgmanager.backend.user.domain.model.UserDomain;
import com.rpgmanager.backend.user.domain.repository.UserRepositoryPort;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

  @Mock private UserRepositoryPort userRepository;

  @InjectMocks private UserDetailsServiceImpl userDetailsService;

  @Test
  void loadUserByUsername_shouldReturnUserDetails() {
    UserDomain user = Instancio.create(UserDomain.class);
    user.setUsername("testuser");
    user.setRole(UserDomain.Role.PLAYER);

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    UserDetails result = userDetailsService.loadUserByUsername("testuser");

    assertThat(result).isNotNull();
    assertThat(result.getUsername()).isEqualTo("testuser");
    assertThat(result.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_PLAYER"));
  }

  @Test
  void loadUserByUsername_shouldThrowException_whenUserNotFound() {
    when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("User not found");
  }
}
