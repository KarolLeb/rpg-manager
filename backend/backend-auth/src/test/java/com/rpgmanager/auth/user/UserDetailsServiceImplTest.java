package com.rpgmanager.auth.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.rpgmanager.auth.user.domain.model.UserDomain;
import com.rpgmanager.auth.user.domain.repository.UserRepositoryPort;
import java.util.Optional;
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
  void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
    UserDomain user = new UserDomain();
    user.setUsername("testuser");
    user.setPassword("password");
    user.setRole(UserDomain.Role.PLAYER);

    given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));

    UserDetails result = userDetailsService.loadUserByUsername("testuser");

    assertThat(result.getUsername()).isEqualTo("testuser");
    assertThat(result.getPassword()).isEqualTo("password");
    assertThat(result.getAuthorities()).hasSize(1);
    assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_PLAYER");
  }

  @Test
  void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
    given(userRepository.findByUsername("unknown")).willReturn(Optional.empty());

    assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
        .isInstanceOf(UsernameNotFoundException.class);
  }

  @Test
  void loadUserByUsername_shouldThrowException_whenUsernameIsEmpty() {
    assertThatThrownBy(() -> userDetailsService.loadUserByUsername(""))
        .isInstanceOf(UsernameNotFoundException.class);
  }
}
