package com.sy.coladay.error

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletResponse

/**
 * Capture unhandled exceptions and turn them into an appropriate http response. The intent is to
 * hide the implementation details to the client and improve the user experience by providing
 * meaningful error message.
 *
 * @author selim
 */
@ControllerAdvice
class BaseErrorController {

    @ExceptionHandler(value = [DataIntegrityViolationException::class])
    fun handleDataIntegrityViolationException(res: HttpServletResponse) {
        res.sendError(
            HttpStatus.CONFLICT.value(),
            "The data presented are in conflict with existing ones"
        )
    }
}