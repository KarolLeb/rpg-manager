package com.rpgmanager.backend.auth;

import com.rpgmanager.backend.security.JwtUtil;
import com.rpgmanager.backend.user.domain.model.UserDomain;
import com.rpgmanager.backend.user.domain.repository.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("pass");

        UserDomain user = new UserDomain();
        user.setUsername("user");
        user.setRole(UserDomain.Role.PLAYER);

        given(userRepository.findByUsername("user")).willReturn(Optional.of(user));
        given(jwtUtil.generateToken("user")).willReturn("token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token", response.getToken());
        assertEquals("user", response.getUsername());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundDuringLogin() {
        LoginRequest request = new LoginRequest("unknown", "pass");
        given(userRepository.findByUsername("unknown")).willReturn(Optional.empty());

        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class, 
            () -> authService.login(request));
    }

    @Test
    void shouldRegisterSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("pass");
        request.setEmail("new@example.com");

        given(userRepository.findByUsername("newuser")).willReturn(Optional.empty());
        given(passwordEncoder.encode("pass")).willReturn("encodedPass");

        authService.register(request);

        verify(userRepository).save(any(UserDomain.class));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringExistingUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");

        given(userRepository.findByUsername("existing")).willReturn(Optional.of(new UserDomain()));

        assertThrows(RuntimeException.class, () -> authService.register(request));
    }
}
