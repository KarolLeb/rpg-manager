package com.rpgmanager.backend.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.rpgmanager.backend.user.domain.model.UserDomain;
import com.rpgmanager.backend.user.domain.repository.UserRepositoryPort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class UserDetailsServiceImplTest {

  private UserDetailsServiceImpl userDetailsService;

  @Mock private UserRepositoryPort userRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userDetailsService = new UserDetailsServiceImpl(userRepository);
  }

  @Test
  void shouldLoadUserByUsername() {
    UserDomain user = new UserDomain();
    user.setUsername("testuser");
    user.setPassword("password");
    user.setRole(UserDomain.Role.PLAYER);
    given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));

    UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

    assertNotNull(userDetails);
    assertEquals("testuser", userDetails.getUsername());
    assertEquals("password", userDetails.getPassword());
    assertTrue(
        userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_PLAYER")));
  }

  @Test
  void shouldThrowExceptionIfUserNotFound() {
    given(userRepository.findByUsername("unknown")).willReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("unknown"));
  }
}
