package com.sy.coladay.security

import com.sy.coladay.user.Role
import com.sy.coladay.user.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.authority.SimpleGrantedAuthority

@ExtendWith(MockitoExtension::class)
internal class UserPrincipalTest {

    @Mock
    var user: User? = null

    @Test
    fun createUserPrincipal_fromUser_ok() {
        `when`(user!!.name).thenReturn("username")
        `when`(user!!.password).thenReturn("password")
        `when`(user!!.role).thenReturn(Role.USER)
        val underTest = UserPrincipal(user!!)

        val expectedRole = SimpleGrantedAuthority(Role.USER.name)
        assertAll(
            Executable { assertThat((underTest as UserPrincipal).user).isSameAs(user) },
            Executable { assertThat(underTest.getUsername()).isEqualTo(user!!.name) },
            Executable { assertThat(underTest.getPassword()).isEqualTo(user!!.password) },
            Executable { assertThat(underTest.getAuthorities()).hasSize(1) },
            Executable { assertThat(expectedRole).isIn(underTest.getAuthorities()) }
        )
    }
}