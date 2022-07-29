package com.sy.coladay.reservation

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Thrown when a user has reached its reservation quota.
 *
 * @author selim
 */
@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Quota limit reached")
class QuotaLimitReachedException
/**
 * Creates a new instance of `QuotaLimitReachedException` with a detail message.
 *
 * @param message the detailed message
 */
    (message: String?) : RuntimeException(message)