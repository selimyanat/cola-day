package com.coladay.error;

import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

/**
 * Test class for {@link BaseErrorController}
 */
@ExtendWith(MockitoExtension.class)
public class BaseErrorControllerTest {

  @Mock
  private HttpServletResponse response;

  @InjectMocks
  private BaseErrorController baseErrorController;

  @Test
  @SneakyThrows
  public void handleDataIntegrityViolationException_return_409() {

    baseErrorController.handleDataIntegrityViolationException(response);
    verify(response).sendError(HttpStatus.CONFLICT.value(),
        "The data presented are in conflict with existing ones");
  }
}