package com.sy.coladay.reservation;

import static java.lang.String.format;

import com.sy.coladay.security.AuthenticationFacade;
import com.sy.coladay.user.User;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

/**
 * Event handler around auto generated reservation repository.
 */
@Component
@RepositoryEventHandler(Reservation.class)
@AllArgsConstructor
@Slf4j
@SuppressWarnings("unused")
class ReservationRepositoryListener {

  private final AuthenticationFacade authenticationHelper;

  /**
   * Augment the reservation with the <code>Principal</code> being authenticated.
   *
   * @param reservation
   *     the reservation to create
   */
  @HandleBeforeCreate
  @SuppressWarnings("unused")
  void handleReservationBeforeCreate(Reservation reservation) {
    final User authenticatedUser = getAuthenticatedUser();
    LOG.debug("Augmenting reservation with organizer {} from security context ",
        authenticatedUser.getName());
    reservation.setOrganizer(authenticatedUser);
  }

  /**
   * Assert that the <code>Principal</code> being authenticated is the reservation owner to allow
   * the reservation deletion.
   *
   * @param reservation
   *     the reservation to delete
   */
  @HandleBeforeDelete
  @SuppressWarnings("unused")
  void handleReservationBeforeDelete(Reservation reservation) {
    final User authenticatedUser = getAuthenticatedUser();
    final Predicate<User> isDeletionAllowed = user -> reservation.getOrganizer().equals(user);
    if (isDeletionAllowed.negate().test(authenticatedUser)) {
      LOG.info("Reservation {} deletion is forbidden for user {}. Reservation can only be deleted "
                   + "by its owner",
          reservation.getId(),
          authenticatedUser
      );
      throw new ReservationCancellationForbiddenException(
          format("User %s is not allowed to delete this reservation %d",
              authenticatedUser.getName(), reservation.getId()));
    }
  }

  private User getAuthenticatedUser() {
    return authenticationHelper.getCurrentUserPrincipal().getUser();
  }
}
