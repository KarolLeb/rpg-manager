package com.rpgmanager.admin.security;

import com.rpgmanager.admin.user.domain.model.UserDomain;
import com.rpgmanager.admin.user.domain.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Implementation of UserDetailsService for Spring Security. */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepositoryPort userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDomain user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    return new User(
        user.getUsername(),
        user.getPassword(),
        user.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
            .collect(java.util.stream.Collectors.toList()));
  }
}
