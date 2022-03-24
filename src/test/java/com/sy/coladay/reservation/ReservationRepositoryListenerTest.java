package com.sy.coladay.reservation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sy.coladay.security.AuthenticationFacade;
import com.sy.coladay.security.UserPrincipal;
import com.sy.coladay.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for {@link ReservationRepository}.
 */
@ExtendWith(MockitoExtension.class)
class ReservationRepositoryListenerTest {

  @Mock
  Reservation reservation;

  @Mock
  UserPrincipal userPrincipal;

  @Mock
  User user;

  @Mock
  AuthenticationFacade authenticationHelper;

  @InjectMocks
  ReservationRepositoryListener underTest;

  @BeforeEach
  void setUp() {
    when(authenticationHelper.getCurrentUserPrincipal()).thenReturn(userPrincipal);
  }

  @Test
  void handleReservationBeforeCreate_ok() {
    when(authenticationHelper.getCurrentUserPrincipal().getUser()).thenReturn(user);

    assertDoesNotThrow(() -> underTest.handleReservationBeforeCreate(reservation));
    verify(reservation).setOrganizer(user);
  }

  @Test
  void handleReservationBeforeDelete_byOrganizer_deletionAllowed() {
    when(authenticationHelper.getCurrentUserPrincipal().getUser()).thenReturn(user);

    assertDoesNotThrow(() -> underTest.handleReservationBeforeCreate(reservation));
  }


  @Test
  void handleReservationBeforeDelete_notByTheOrganizer_throwAnException() {
    final User aUser = mock(User.class);
    when(authenticationHelper.getCurrentUserPrincipal().getUser()).thenReturn(aUser);
    when(reservation.getOrganizer()).thenReturn(user);

    assertThatThrownBy(() -> underTest
        .handleReservationBeforeDelete(reservation))
        .isInstanceOf(ReservationCancellationForbiddenException.class)
        .hasMessage("User %s is not allowed to delete this reservation %d",
                    aUser.getName(),
                    reservation.getId()
        );
  }
}