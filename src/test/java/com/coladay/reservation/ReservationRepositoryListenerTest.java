package com.coladay.reservation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coladay.security.AuthenticationFacade;
import com.coladay.security.UserPrincipal;
import com.coladay.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for {@link ReservationRepository}
 */
@ExtendWith(MockitoExtension.class)
public class ReservationRepositoryListenerTest {

  @Mock
  private Reservation reservation;

  @Mock
  private UserPrincipal userPrincipal;

  @Mock
  private User user;

  @Mock
  private AuthenticationFacade authenticationHelper;

  @InjectMocks
  private ReservationRepositoryListener reservationRepositoryListener;

  @BeforeEach
  public void setUp() {
    when(authenticationHelper.getCurrentUserPrincipal()).thenReturn(userPrincipal);
  }

  @Test
  public void handleReservationBeforeCreate_ok() {
    when(authenticationHelper.getCurrentUserPrincipal().getUser()).thenReturn(user);

    reservationRepositoryListener.handleReservationBeforeCreate(reservation);
    verify(reservation).setOrganizer(user);
  }

  @Test
  public void handleReservationBeforeDelete_byOrganizer_deletionAllowed() {
    when(authenticationHelper.getCurrentUserPrincipal().getUser()).thenReturn(user);
    when(reservation.getOrganizer()).thenReturn(user);

    reservationRepositoryListener.handleReservationBeforeDelete(reservation);
  }


  @Test
  public void handleReservationBeforeDelete_notByTheOrganizer_throwAnException() {
    final User aUser = mock(User.class);
    when(authenticationHelper.getCurrentUserPrincipal().getUser()).thenReturn(aUser);
    when(reservation.getOrganizer()).thenReturn(user);

    assertThatThrownBy(() -> reservationRepositoryListener
        .handleReservationBeforeDelete(reservation))
        .isInstanceOf(ReservationCancellationForbiddenException.class)
        .hasMessage("User %s is not allowed to delete this reservation %d",
            aUser.getName(),
            reservation.getId());
  }
}