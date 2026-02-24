package com.rpgmanager.auth.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.rpgmanager.auth.user.domain.model.UserDomain;
import com.rpgmanager.auth.user.domain.repository.UserRepositoryPort;
import com.rpgmanager.common.security.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private JwtUtil jwtUtil;
  @Mock
  private UserRepositoryPort userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;

  private AuthService authService;

  @BeforeEach
  void setUp() {
    authService = new AuthService(authenticationManager, jwtUtil, userRepository, passwordEncoder);
  }

  @Test
  void login_shouldReturnAuthResponse_whenCredentialsAreValid() {
    LoginRequest request = new LoginRequest("testuser", "password");
    UserDomain user = new UserDomain();
    user.setId(1L);
    user.setUsername("testuser");
    user.setRoles(java.util.Collections.singleton(UserDomain.Role.PLAYER));

    given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
    given(jwtUtil.generateToken("testuser", 1L, java.util.List.of("PLAYER"))).willReturn("token");

    AuthResponse response = authService.login(request);

    assertThat(response.getToken()).isEqualTo("token");
    assertThat(response.getUsername()).isEqualTo("testuser");
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  void login_shouldThrowException_whenAuthenticationFails() {
    LoginRequest request = new LoginRequest("testuser", "wrong");
    given(authenticationManager.authenticate(any()))
        .willThrow(new BadCredentialsException("Bad credentials"));

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(BadCredentialsException.class);
  }

  @Test
  void login_shouldThrowException_whenUserNotFoundAfterAuth() {
    LoginRequest request = new LoginRequest("testuser", "password");
    given(userRepository.findByUsername("testuser")).willReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(UsernameNotFoundException.class);
  }

  @Test
  @SuppressWarnings("null")
  void register_shouldSaveUser_whenUsernameIsAvailable() {
    RegisterRequest request = new RegisterRequest("newuser", "password", "test@example.com");
    given(userRepository.findByUsername("newuser")).willReturn(Optional.empty());
    given(passwordEncoder.encode("password")).willReturn("encoded");

    authService.register(request);

    org.mockito.ArgumentCaptor<UserDomain> userCaptor = org.mockito.ArgumentCaptor.forClass(UserDomain.class);
    verify(userRepository).save(userCaptor.capture());
    UserDomain savedUser = userCaptor.getValue();
    assertThat(savedUser.getUsername()).isEqualTo("newuser");
    assertThat(savedUser.getPassword()).isEqualTo("encoded");
    assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    assertThat(savedUser.getRoles()).containsExactly(UserDomain.Role.PLAYER);
  }

  @Test
  void register_shouldThrowException_whenUsernameExists() {
    RegisterRequest request = new RegisterRequest("existing", "password", "test@example.com");
    given(userRepository.findByUsername("existing")).willReturn(Optional.of(new UserDomain()));

    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void login_shouldThrowException_whenUsernameIsEmpty() {
    LoginRequest request = new LoginRequest("", "password");
    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void register_shouldThrowException_whenUsernameIsEmpty() {
    RegisterRequest request = new RegisterRequest("", "password", "test@example.com");
    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
