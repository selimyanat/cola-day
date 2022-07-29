package com.sy.coladay.reservation

import com.sy.coladay.security.AuthenticationFacade
import com.sy.coladay.user.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.HandleBeforeDelete
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component
import java.util.function.Predicate

/**
 * Event handler around auto generated reservation repository.
 */
@Component
@RepositoryEventHandler
class ReservationRepositoryListener (private val authenticationHelper: AuthenticationFacade) {

    /**
     * Augment the reservation with the `Principal` being authenticated.
     *
     * @param reservation the reservation to create
     */
    @HandleBeforeCreate
    fun handleReservationBeforeCreate(reservation: Reservation) {
        val authenticatedUser = getAuthenticatedUser()
        LOG.debug(
            "Augmenting reservation with organizer {} from security context ",
            authenticatedUser.name
        )
        reservation.organizer = authenticatedUser
    }

    /**
     * Assert that the `Principal` being authenticated is the reservation owner to allow
     * the reservation deletion.
     *
     * @param reservation the reservation to delete
     */
    @HandleBeforeDelete
    fun handleReservationBeforeDelete(reservation: Reservation) {
        val authenticatedUser = getAuthenticatedUser()
        val isDeletionAllowed = Predicate { user: User? -> reservation.organizer.equals(user) }
        if (isDeletionAllowed.negate().test(authenticatedUser)) {
            LOG.info(
                "Reservation {} deletion is forbidden for user {}. Reservation can only be deleted "
                        + "by its owner",
                reservation.id,
                authenticatedUser
            )
            throw ReservationCancellationForbiddenException(
                String.format(
                    "User %s is not allowed to delete this reservation %d",
                    authenticatedUser.name, reservation.id
                )
            )
        }
    }

    private fun getAuthenticatedUser():User {
        return authenticationHelper!!.getCurrentUserPrincipal().user;
    }


    companion object {

        private val LOG: Logger = LoggerFactory.getLogger(ReservationRepositoryListener::class.java)
    }
}