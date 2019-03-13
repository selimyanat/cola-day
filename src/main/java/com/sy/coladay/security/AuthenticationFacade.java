package com.sy.coladay.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Facade that provide easy access to the current <code>UserPrincipal</code>.
 *
 * @author selim
 * @see UserPrincipal
 * @see SecurityContextHolder
 */
@Component
public class AuthenticationFacade {

  /**
   * Return an instance of the current principal authenticated.
   *
   * @return the current <code>UserPrincipal</code> being authenticated.
   */
  public UserPrincipal getCurrentUserPrincipal() {
    return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

}
