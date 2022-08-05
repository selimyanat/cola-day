package com.sy.coladay.error

import lombok.SneakyThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import javax.servlet.http.HttpServletResponse

/**
 * Test class for [BaseErrorController].
 */
@ExtendWith(MockitoExtension::class)
internal class BaseErrorControllerTest {

    @Mock
    var response: HttpServletResponse? = null

    @InjectMocks
    var baseErrorController: BaseErrorController? = null

    @Test
    @SneakyThrows
    fun `returns a 409 when a data integrity violation exception is raised`() {
        baseErrorController!!.handleDataIntegrityViolationException(response!!)
        verify(response)?.sendError(
            HttpStatus.CONFLICT.value(),
            "The data presented are in conflict with existing ones"
        )
    }
}