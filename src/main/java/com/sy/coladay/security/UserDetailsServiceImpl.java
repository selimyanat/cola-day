package com.sy.coladay.security;

import static java.lang.String.format;

import com.sy.coladay.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Retrieve the <code>UserDetails</code> out of database.
 *
 * @author selim
 */
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
    return userRepository
        .findByName(username)
        .map(UserPrincipal::new)
        .orElseThrow(
            () -> new UsernameNotFoundException(format("Username %s not found", username)));
  }
}
