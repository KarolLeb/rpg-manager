package com.rpgmanager.backend.security;

import java.util.Collection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/** Custom UserDetails to hold additional information like userId. */
@Getter
@EqualsAndHashCode(callSuper = true)
public class UserContext extends User {
  private static final long serialVersionUID = 1L;
  private final Long userId;

  public UserContext(
      String username,
      String password,
      Collection<? extends GrantedAuthority> authorities,
      Long userId) {
    super(username, password, authorities);
    this.userId = userId;
  }
}
