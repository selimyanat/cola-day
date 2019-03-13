package com.sy.coladay.reservation;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static java.lang.String.format;
import static java.util.function.Predicate.isEqual;

import com.sy.coladay.company.Companies;
import io.vavr.control.Try;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * The quota service is a component with the objective to ration how many reservation are created
 * by both companies coke and pepsi.
 *
 * <p>The service sits on top of the underlying spring data rest repository that handles the
 * creation and deletion of reservations.
 * </p>
 *
 * @author selim
 */
@Aspect
@Transactional
@Component
@Slf4j
@SuppressWarnings("unused")
class QuotaService {

  private final ReservationRepository reservationRepository;

  @Getter(AccessLevel.PACKAGE)
  private final Counter cokeCounter;

  @Getter(AccessLevel.PACKAGE)
  private final Counter pepsiCounter;

  /**
   * Creates a new instance of <code>QuotaService</code> .
   * .
   *
   * @param meetingRepository
   *     the meeting repository used to initialize the counters offsets.
   * @param pepsiQuota
   *     the initial reservation quota of pepsi
   * @param cokeQuota
   *     the initial reservation quota of coke
   */
  QuotaService(ReservationRepository meetingRepository,
      @Value("${pepsi.quota}") int pepsiQuota, @Value("${coke.quota}") int cokeQuota) {
    this.reservationRepository = meetingRepository;
    this.cokeCounter = new Counter(
        meetingRepository.countByOrganizerCompany(Companies.COKE).intValue(), cokeQuota);
    this.pepsiCounter = new Counter(
        meetingRepository.countByOrganizerCompany(Companies.PEPSI).intValue(), pepsiQuota);
  }

  /**
   * Assert that the reservations quota is not reached then delegates the reservation creation and
   * finally increment the reservation counter.
   * Otherwise abort the reservation creation by throwing a <code>QuotaLimitReachedException</code>
   *
   * @param pjp
   *     the joint point
   * @return the reservation created
   * @throws Throwable
   *     if the reservations quota is reached or if the reservation creation has
   *     failed.
   */
  @Around("execution(*  ReservationRepository.save(..))")
  Reservation interceptSaveOperation(final ProceedingJoinPoint pjp) throws Throwable {

    final Reservation reservation = (Reservation) pjp.getArgs()[0];
    final Counter counter = getCounter(reservation.getOrganizer().getCompany());

    final Predicate<Reservation> isQuotaLimitReached = meeting1 -> counter.increment();
    if (isQuotaLimitReached.negate().test(reservation)) {
      throw new QuotaLimitReachedException(String.format("Reservation for %s company cannot be created "
              + "because the quota limit is already reached.",
          reservation.getOrganizer().getCompany()));
    }

    return (Reservation) Try.of(() -> pjp.proceed(pjp.getArgs()))
        .onFailure(throwable -> {
          LOG.info("Could not create reservation for {} ", reservation.getOrganizer().getCompany());
          counter.decrement();
        })
        .getOrElseThrow(throwable -> throwable);
  }


  /**
   * Delegates the reservation deletion and decrement the reservation counter accordingly..
   *
   * @param pjp
   *     the joint point
   */
  @Around("execution(*  ReservationRepository.deleteById(..))")
  @SneakyThrows
  void interceptDeleteOperation(final ProceedingJoinPoint pjp) {

    final long meetingId = (Long) pjp.getArgs()[0];
    final Optional<Reservation> meeting = reservationRepository.findById(meetingId);

    if (meeting.isPresent()) {
      pjp.proceed(pjp.getArgs());
      getCounter(meeting.get().getOrganizer().getCompany()).decrement();
    } else {
      // May happen on a rare circumstances because Spring has already made that check before
      // calling this method but there's a small window when this could happen.
      LOG.debug("The reservation with id {} seems to be already deleted by a concurrent thread, "
              + "execute delete operation anyways and let the error handling layer take care of it",
          meetingId);
      pjp.proceed(pjp.getArgs());
    }
  }

  private Counter getCounter(final Companies company) {
    return Match(company).of(
        Case($(isEqual(Companies.COKE)), cokeCounter),
        Case($(isEqual(Companies.PEPSI)), pepsiCounter));
  }

}
