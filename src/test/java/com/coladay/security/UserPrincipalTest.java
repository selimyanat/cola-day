package com.coladay.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.coladay.user.Role;
import com.coladay.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class UserPrincipalTest {

  @Mock
  private User user;

  private UserDetails userPrincipal;

  @Test
  public void createUserPrincipal_fromUser_ok() {

    when(user.getName()).thenReturn("username");
    when(user.getPassword()).thenReturn("password");
    when(user.getRole()).thenReturn(Role.USER);

    userPrincipal = new UserPrincipal(user);

    assertThat(((UserPrincipal) userPrincipal).getUser()).isSameAs(user);
    assertThat(userPrincipal.getUsername()).isEqualTo(user.getName());
    assertThat(userPrincipal.getPassword()).isEqualTo(user.getPassword());
    assertThat(userPrincipal.getAuthorities()).hasSize(1);
    assertThat(
        userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority(Role.USER.name())));
  }

}