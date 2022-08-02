package com.sy.coladay.reservation

import com.sy.coladay.security.AuthenticationFacade
import com.sy.coladay.security.UserPrincipal
import com.sy.coladay.user.User
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

/**
 * Test class for [ReservationRepository].
 */
@ExtendWith(MockitoExtension::class)
internal class ReservationRepositoryListenerTest {
    @Mock
    var reservation: Reservation? = null

    @Mock
    var userPrincipal: UserPrincipal? = null

    @Mock
    var user: User? = null

    @Mock
    var authenticationHelper: AuthenticationFacade? = null

    @InjectMocks
    var underTest: ReservationRepositoryListener? = null

    @BeforeEach
    fun setUp() {
        `when`(authenticationHelper!!.getCurrentUserPrincipal()).thenReturn(userPrincipal)
    }

    @Test
    fun `handle reservation creation sets the organiser`() {
        `when`(authenticationHelper!!.getCurrentUserPrincipal().user).thenReturn(user)

        assertDoesNotThrow { underTest!!.handleReservationBeforeCreate(reservation!!)}
        verify(reservation)?.organizer = user!!
    }

    @Test
    fun `handle reservation deletion by the organiser is allowed`() {
        `when`(authenticationHelper!!.getCurrentUserPrincipal().user).thenReturn(user)

        assertDoesNotThrow { underTest!!.handleReservationBeforeCreate(reservation!!)}
    }

    @Test
    fun `handle reservation deletion is not allowed if not done by the organiser`() {
        val aUser = mock(User::class.java)
        `when`(authenticationHelper!!.getCurrentUserPrincipal().user).thenReturn(aUser)
        `when`(reservation!!.organizer).thenReturn(user)

        assertThatThrownBy { underTest?.handleReservationBeforeDelete(reservation!!)}
            .isInstanceOf(ReservationCancellationForbiddenException::class.java)
            .hasMessage(
                "User %s is not allowed to delete this reservation %d",
                aUser.name,
                reservation!!.id
            )
    }
}