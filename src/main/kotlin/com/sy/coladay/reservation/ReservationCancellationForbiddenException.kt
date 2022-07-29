package com.sy.coladay.reservation

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Thrown when a user that is not the reservation owner tries to cancel the reservation.
 *
 * @author selim
 */
@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Forbidden reservation cancellation")
class ReservationCancellationForbiddenException
/**
 * Creates a new instance of `ReservationCancellationForbiddenException` with a detail
 * message.
 *
 * @param message the detailed message
 */
    (message: String?) : RuntimeException(message)