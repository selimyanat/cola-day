package com.coladay.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a user that is not the reservation owner tries to cancel the reservation.
 *
 * @author selim
 */
@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Forbidden reservation cancellation")
public class ReservationCancellationForbiddenException extends RuntimeException {

  /**
   * Creates a new instance of <code>ReservationCancellationForbiddenException</code> with a
   * detail message.
   *
   * @param message
   *     the detailed message
   */
  public ReservationCancellationForbiddenException(String message) {
    super(message);
  }

}
