package com.sy.coladay.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.sy.coladay.user.Role;
import com.sy.coladay.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class UserPrincipalTest {

  @Mock
  User user;

  private UserDetails underTest;

  @Test
  void createUserPrincipal_fromUser_ok() {

    when(user.getName()).thenReturn("username");
    when(user.getPassword()).thenReturn("password");
    when(user.getRole()).thenReturn(Role.USER);

    underTest = new UserPrincipal(user);
    Assertions.assertThat(((UserPrincipal) underTest).getUser()).isSameAs(user);
    assertThat(underTest.getUsername()).isEqualTo(user.getName());
    assertThat(underTest.getPassword()).isEqualTo(user.getPassword());
    assertThat(underTest.getAuthorities()).hasSize(1);
    assertThat(
        underTest.getAuthorities().contains(new SimpleGrantedAuthority(Role.USER.name())));
  }

}