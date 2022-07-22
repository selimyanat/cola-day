package com.sy.coladay.security

import com.sy.coladay.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails

/**
 * Implementation of [UserDetails].
 */
class UserPrincipal(val user: User) : UserDetails {


    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return AuthorityUtils.createAuthorityList(user.role.name)
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.name!!
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}