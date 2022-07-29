package com.sy.coladay.reservation

import com.sy.coladay.company.Companies
import io.vavr.API
import io.vavr.control.Try
import lombok.SneakyThrows
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.function.Predicate

/**
 * The quota service is a component with the objective to ration how many reservation are created by
 * both companies coke and pepsi.
 *
 *
 * The service sits on top of the underlying spring data rest repository that handles the
 * creation and deletion of reservations.
 *
 *
 * @author selim
 */
@Aspect
@Transactional
@Component
open class QuotaService(
    private val reservationRepository: ReservationRepository,
    @Value("\${pepsi.quota}") pepsiQuota: Int,
    @Value("\${coke.quota}") cokeQuota: Int
) {

    // TODO Hide implementation details
    var cokeCounter: Counter

    // TODO Hide implementation details
    var pepsiCounter: Counter

    /**
     * Creates a new instance of `QuotaService` . .
     *
     * @param meetingRepository the meeting repository used to initialize the counters offsets.
     * @param pepsiQuota the initial reservation quota of pepsi
     * @param cokeQuota the initial reservation quota of coke
     */
    init {
        this.cokeCounter = Counter(
            this.reservationRepository.countByOrganizerCompany(Companies.COKE).toInt(), cokeQuota
        )
        this.pepsiCounter = Counter(
            this.reservationRepository.countByOrganizerCompany(Companies.PEPSI).toInt(), pepsiQuota
        )
    }

    /**
     * Assert that the reservations quota is not reached then delegates the reservation creation and
     * finally increment the reservation counter. Otherwise abort the reservation creation by throwing
     * a `QuotaLimitReachedException`
     *
     * @param pjp the joint point
     * @return the reservation created
     * @throws Throwable if the reservations quota is reached or if the reservation creation has
     * failed.
     */
    @Around("execution(*  ReservationRepository.save(..))")
    @Throws(Throwable::class)
    fun interceptSaveOperation(pjp: ProceedingJoinPoint): Reservation {
        val reservation = pjp.args[0] as Reservation
        val counter = getCounter(reservation.organizer.company)
        val isQuotaLimitReached = Predicate { meeting1: Reservation? -> counter.increment() }
        if (isQuotaLimitReached.negate().test(reservation)) {
            throw QuotaLimitReachedException(
                "Reservation for " + reservation.organizer.company
                        + " company cannot be created because the quota limit is already reached."
            )
        }
        return Try.of { pjp.proceed(pjp.args) }
            .onFailure { throwable: Throwable? ->
                LOG.info(
                    "Could not create reservation for {} ",
                    reservation.organizer.company
                )
                counter.decrement()
            }
            .getOrElseThrow { throwable: Throwable? -> throwable } as Reservation
    }

    /**
     * Delegates the reservation deletion and decrement the reservation counter accordingly.
     *
     * @param pjp the joint point
     */
    @Around("execution(*  ReservationRepository.deleteById(..))")
    @SneakyThrows
    fun interceptDeleteOperation(pjp: ProceedingJoinPoint) {
        val meetingId = pjp.args[0] as Long
        val meeting = reservationRepository.findById(meetingId)
        if (meeting.isPresent) {
            pjp.proceed(pjp.args)
            getCounter(meeting.get().organizer.company).decrement()
        } else {
            // May happen on a rare circumstances because Spring has already made that check before
            // calling this method but there's a small window when this could happen.
            LOG.debug(
                "The reservation with id {} seems to be already deleted by a concurrent thread, "
                        + "execute delete operation anyways and let the error handling layer take care "
                        + "of it",
                meetingId
            )
            pjp.proceed(pjp.args)
        }
    }

    // TODO return a copy of the counter
    private fun getCounter(company: Companies): Counter {
        return API.Match(company).of(
            API.Case(API.`$`(Predicate.isEqual<Any>(Companies.COKE)), cokeCounter),
            API.Case(API.`$`(Predicate.isEqual<Any>(Companies.PEPSI)), pepsiCounter)
        )
    }

    companion object {

        private val LOG: Logger = LoggerFactory.getLogger(QuotaService::class.java)
    }
}