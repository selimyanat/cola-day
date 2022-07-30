package com.sy.coladay.security

import com.sy.coladay.user.User
import com.sy.coladay.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.*

/**
 * Test class for [UserDetailsServiceImpl].
 *
 * @author selim
 */
@ExtendWith(MockitoExtension::class)
internal class UserDetailsServiceImplTest() {

    @Mock
    var userRepository: UserRepository? = null

    @InjectMocks
    var underTest: UserDetailsServiceImpl? = null

    @Test
    fun `load user by username when user exists return user principal`() {
        `when`(userRepository!!.findByName(ArgumentMatchers.anyString()))
            .thenReturn(Optional.of(mock(User::class.java)))

        val userDetails = underTest!!.loadUserByUsername("user")
        assertThat(userDetails)
            .isNotNull
            .isInstanceOf(UserPrincipal::class.java)
    }

    @Test
    fun `load user by username when user does not exist throws an exception`() {
        `when`(userRepository!!.findByName(ArgumentMatchers.anyString()))
            .thenReturn(Optional.empty()
        )

        assertThatThrownBy { underTest!!.loadUserByUsername("user") }
            .isInstanceOf(UsernameNotFoundException::class.java)
            .hasMessage("Username user not found")
    }
}