package com.sy.coladay.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a user has reached its reservation quota.
 *
 * @author selim
 */
@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Quota limit reached")
public class QuotaLimitReachedException extends RuntimeException {

  /**
   * Creates a new instance of <code>QuotaLimitReachedException</code> with a detail message.
   *
   * @param message the detailed message
   */
  public QuotaLimitReachedException(String message) {
    super(message);
  }

}
