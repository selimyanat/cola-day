package com.sy.coladay.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import com.sy.coladay.company.Companies;
import com.sy.coladay.user.User;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Test class for {@link QuotaService}.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QuotaServiceTest {

  static final int QUOTA = 2;

  @Mock
  Reservation reservation;

  @Mock
  User user;

  @Mock
  ProceedingJoinPoint proceedingJoinPoint;

  @Mock
  ReservationRepository meetingRepository;

  QuotaService underTest;


  @BeforeEach
  @SneakyThrows
  void setUp() {
    when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{reservation});
    when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs())).thenReturn(reservation);
    when(reservation.getOrganizer()).thenReturn(user);

    when(meetingRepository.countByOrganizerCompany(Companies.COKE)).thenReturn(0L);
    when(meetingRepository.countByOrganizerCompany(Companies.PEPSI)).thenReturn(0L);
    underTest = new QuotaService(meetingRepository, QUOTA, QUOTA);
  }

  @ParameterizedTest
  @EnumSource(Companies.class)
  @SneakyThrows
  void interceptMeetingCreation_quota_incremented_ok(Companies company) {
    final Counter counter = getCounterFromCompany(company);
    when(user.getCompany()).thenReturn(company);
    assertThat(counter.getOffset()).isEqualTo(0);

    assertAll(
        () -> assertThat(underTest.interceptSaveOperation(proceedingJoinPoint))
            .isSameAs(reservation),
        () ->  assertThat(counter.getOffset()).isEqualTo(1));
  }

  @ParameterizedTest
  @EnumSource(Companies.class)
  @SneakyThrows
  void interceptMeetingCreation_quota_exceeded_throwException(Companies company) {

    final Counter counter = getCounterFromCompany(company);
    // Reach the quota limit
    IntStream.of(0, QUOTA).forEach(i -> counter.increment());
    assertThat(counter.getOffset()).isEqualTo(2);
    when(user.getCompany()).thenReturn(company);

    assertThatThrownBy(() -> underTest.interceptSaveOperation(proceedingJoinPoint))
        .isInstanceOf(QuotaLimitReachedException.class)
        .hasMessage("Reservation for %s company cannot be created because the quota limit is "
            + "already reached.", company);
    assertThat(counter.getOffset()).isEqualTo(2);
  }

  @ParameterizedTest
  @EnumSource(Companies.class)
  @SneakyThrows
  void interceptMeetingCreation_databaseException_quota_notModified_ok(Companies company) {

    final Counter counter = getCounterFromCompany(company);
    assertThat(counter.getOffset()).isEqualTo(0);
    when(user.getCompany()).thenReturn(company);
    when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs()))
        .thenThrow(new RuntimeException("Something bad happen"));

    assertThatThrownBy(() -> underTest.interceptSaveOperation(proceedingJoinPoint))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Something bad happen");
    assertThat(counter.getOffset()).isEqualTo(0);
  }

  @ParameterizedTest
  @EnumSource(Companies.class)
  @SneakyThrows
  void interceptMeetingDeletion_quota_decremented_ok(Companies company) {

    final Counter counter = getCounterFromCompany(company);
    counter.increment();
    assertThat(counter.getOffset()).isEqualTo(1);
    when(user.getCompany()).thenReturn(company);
    when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{1L});
    when(meetingRepository.findById((Long) proceedingJoinPoint.getArgs()[0]))
        .thenReturn(Optional.of(reservation));

    underTest.interceptDeleteOperation(proceedingJoinPoint);
    assertThat(counter.getOffset()).isEqualTo(0);
  }

  @ParameterizedTest
  @EnumSource(Companies.class)
  @SneakyThrows
  void interceptMeetingDeletion_meetingAlreadyDeleted_quota_notDecremented_ok(
      Companies company) {

    final Counter counter = getCounterFromCompany(company);
    counter.increment();
    assertThat(counter.getOffset()).isEqualTo(1);
    when(user.getCompany()).thenReturn(company);
    when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{1L});
    when(meetingRepository.findById((Long) proceedingJoinPoint.getArgs()[0]))
        .thenReturn(Optional.empty());

    underTest.interceptDeleteOperation(proceedingJoinPoint);
    assertThat(counter.getOffset()).isEqualTo(1);
  }

  Counter getCounterFromCompany(Companies company) {
    return company.equals(Companies.COKE) ? underTest.getCokeCounter() :
        underTest.getPepsiCounter();
  }

}
