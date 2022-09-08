package com.sy.coladay.security

import com.sy.coladay.user.User
import com.sy.coladay.user.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Retrieve the `UserDetails` out of database.
 *
 * @author selim
 */
@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository
            .findByName(username)
            .map { user: User? -> UserPrincipal(user!!)}
            .orElseThrow {
                UsernameNotFoundException("Username $username not found")
            }
    }
}