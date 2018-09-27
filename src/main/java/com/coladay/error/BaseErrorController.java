package com.coladay.error;

import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Capture unhandled exceptions and turn them into an appropriate http response. The intent is to
 * hide the implementation details to the client and improve the user experience by providing
 * meaningful error message.
 *
 * @author selim
 */
@ControllerAdvice
@SuppressWarnings("unused")
public class BaseErrorController {

  @ExceptionHandler(value = DataIntegrityViolationException.class)
  @SneakyThrows
  void handleDataIntegrityViolationException(HttpServletResponse res) {
    res.sendError(HttpStatus.CONFLICT.value(),
        "The data presented are in conflict with existing ones");
  }
}
