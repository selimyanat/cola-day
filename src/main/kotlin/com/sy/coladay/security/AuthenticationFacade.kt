package com.sy.coladay.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 * Facade that provide easy access to the current `UserPrincipal`.
 *
 * @author selim
 * @see UserPrincipal
 *
 * @see SecurityContextHolder
 */
@Component
class AuthenticationFacade {
    /**
     * Return an instance of the current principal authenticated.
     *
     * @return the current `UserPrincipal` being authenticated.
     */
    fun getCurrentUserPrincipal():UserPrincipal {
        return SecurityContextHolder.getContext().authentication.principal as UserPrincipal
    }
}