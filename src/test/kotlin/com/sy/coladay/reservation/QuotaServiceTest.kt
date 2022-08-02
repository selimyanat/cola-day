package com.sy.coladay.reservation

import com.sy.coladay.company.Companies
import com.sy.coladay.user.User
import lombok.SneakyThrows
import org.aspectj.lang.ProceedingJoinPoint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness
import java.util.*
import java.util.stream.IntStream

/**
 * Test class for [QuotaService].
 */
@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
internal class QuotaServiceTest {

    @Mock
    var reservation: Reservation? = null

    @Mock
    var user: User? = null

    @Mock
    var proceedingJoinPoint: ProceedingJoinPoint? = null

    @Mock
    var meetingRepository: ReservationRepository? = null
    var underTest: QuotaService? = null

    @BeforeEach
    @SneakyThrows
    fun setUp() {
        `when`(proceedingJoinPoint!!.args).thenReturn(arrayOf<Any?>(reservation))
        `when`(proceedingJoinPoint!!.proceed(proceedingJoinPoint!!.args))
            .thenReturn(reservation)
        `when`(reservation!!.organizer).thenReturn(user)
        `when`(meetingRepository!!.countByOrganizerCompany(Companies.COKE)).thenReturn(0L)
        `when`(meetingRepository!!.countByOrganizerCompany(Companies.PEPSI)).thenReturn(0L)

        underTest = QuotaService(meetingRepository!!, QUOTA, QUOTA)
    }

    @ParameterizedTest
    @EnumSource(Companies::class)
    @SneakyThrows
    fun `intercept meeting creation increment company's quota`(company: Companies) {
        val counter = getCounterFromCompany(company)
        `when`(user!!.company).thenReturn(company)

        assertThat(counter.getOffset()).isZero
        assertAll(
            Executable { assertThat(underTest!!.interceptSaveOperation(proceedingJoinPoint!!))
                    .isSameAs(reservation) },
            Executable {assertThat(counter.getOffset()).isEqualTo(1) }
        )
    }

    @ParameterizedTest
    @EnumSource(Companies::class)
    @SneakyThrows
    fun `throw exception when meeting creation gets above quota`(company:Companies) {
        val counter = getCounterFromCompany(company)
        // Reach the quota limit
        IntStream.of(0, QUOTA).forEach { i: Int -> counter.increment() }
        assertThat(counter.getOffset()).isEqualTo(2)
        `when`(user!!.company).thenReturn(company)

        assertThatThrownBy { underTest!!.interceptSaveOperation(proceedingJoinPoint!!) }
            .isInstanceOf(QuotaLimitReachedException::class.java)
            .hasMessage(
                "Reservation for %s company cannot be created because the quota limit is "
                        + "already reached.", company
            )
        assertThat(counter.getOffset()).isEqualTo(2)
    }

    @ParameterizedTest
    @EnumSource(Companies::class)
    @SneakyThrows
    fun `quota not modified if a database exception is thrown while creating meeting`(company:
                                                                                      Companies) {
        val counter = getCounterFromCompany(company)
        assertThat(counter.getOffset()).isZero
        `when`(user!!.company).thenReturn(company)
        `when`(proceedingJoinPoint!!.proceed(proceedingJoinPoint!!.args))
            .thenThrow(RuntimeException("Something bad happen"))

        assertThatThrownBy { underTest!!.interceptSaveOperation(proceedingJoinPoint!!) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Something bad happen")
        assertThat(counter.getOffset()).isZero
    }

    @ParameterizedTest
    @EnumSource(Companies::class)
    @SneakyThrows
    fun `intercept meeting deletion decrement quota`(company: Companies) {
        val counter = getCounterFromCompany(company)
        counter.increment()
        assertThat(counter.getOffset()).isEqualTo(1)
        `when`(user!!.company).thenReturn(company)
        `when`(proceedingJoinPoint!!.args).thenReturn(arrayOf<Any>(1L))
        `when`(meetingRepository!!.findById(proceedingJoinPoint!!.args[0] as Long))
            .thenReturn(Optional.of(reservation!!))

        underTest!!.interceptDeleteOperation(proceedingJoinPoint!!)
        assertThat(counter.getOffset()).isZero
    }

    @ParameterizedTest
    @EnumSource(Companies::class)
    @SneakyThrows
    fun `intercept already deleted meeting does not decrement quota`(company: Companies) {
        val counter = getCounterFromCompany(company)
        counter.increment()
        assertThat(counter.getOffset()).isEqualTo(1)
        `when`(user!!.company).thenReturn(company)
        `when`(proceedingJoinPoint!!.args).thenReturn(arrayOf<Any>(1L))
        `when`(meetingRepository!!.findById(proceedingJoinPoint!!.args[0] as Long))
            .thenReturn(Optional.empty())

        underTest!!.interceptDeleteOperation(proceedingJoinPoint!!)
        assertThat(counter.getOffset()).isEqualTo(1)
    }

    private fun getCounterFromCompany(company: Companies): Counter {
        return if (company == Companies.COKE) underTest!!.cokeCounter else underTest!!.pepsiCounter
    }

    companion object {
        const val QUOTA = 2
    }
}