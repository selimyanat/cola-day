package com.coladay.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.coladay.company.Companies;
import com.coladay.user.User;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Test class for {@link QuotaService}
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class QuotaServiceTest {

  private static final int QUOTA = 2;

  @Mock
  private Reservation reservation;

  @Mock
  private User user;

  @Mock
  private ProceedingJoinPoint proceedingJoinPoint;

  @Mock
  private ReservationRepository meetingRepository;

  private QuotaService quotaService;


  @BeforeEach
  @SneakyThrows
  public void setUp() {

    when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{reservation});
    when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs())).thenReturn(reservation);
    when(reservation.getOrganizer()).thenReturn(user);

    when(meetingRepository.countByOrganizerCompany(Companies.COKE)).thenReturn(0l);
    when(meetingRepository.countByOrganizerCompany(Companies.PEPSI)).thenReturn(0l);
    quotaService = new QuotaService(meetingRepository, QUOTA, QUOTA);
  }

  @ParameterizedTest
  @EnumSource(Companies.class)
  @SneakyThrows
  public void interceptMeetingCreation_quota_incremented_ok(Companies company) {

    final Counter counter = getCounterFromCompany(company);
    when(user.getCompany()).thenReturn(company);
    assertThat(counter.getOffset()).isEqualTo(0);

    assertThat(quotaService.interceptSaveOperation(proceedingJoinPoint)).isSameAs(reservation);
    assertThat(counter.getOffset()).isEqualTo(1);
  }

  @ParameterizedTest
  @EnumSource(Companies.class)
  @SneakyThrows
  public void interceptMeetingCreation_quota_exceeded_throwException(Companies company) {

    final Counter counter = getCounterFromCompany(company);
    // Reach the quota limit
    IntStream.of(0, QUOTA).forEach(i -> counter.increment());
    assertThat(counter.getOffset()).isEqualTo(2);
    when(user.getCompany()).thenReturn(company);

    assertThatThrownBy(() -> quotaService.interceptSaveOperation(proceedingJoinPoint))
        .isInstanceOf(QuotaLimitReachedException.class)
        .hasMessage("Reservation for %s company cannot be created because the quota limit is "
            + "already reached.", company);
    assertThat(counter.getOffset()).isEqualTo(2);
  }

  @ParameterizedTest
  @EnumSource(Companies.class)
  @SneakyThrows
  public void interceptMeetingCreation_databaseException_quota_notModified_ok(Companies company) {

    final Counter counter = getCounterFromCompany(company);
    assertThat(counter.getOffset()).isEqualTo(0);
    when(user.getCompany()).thenReturn(company);
    when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs()))
        .thenThrow(new RuntimeException("Something bad happen"));

    assertThatThrownBy(() -> quotaService.interceptSaveOperation(proceedingJoinPoint))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Something bad happen");
    assertThat(counter.getOffset()).isEqualTo(0);
  }

  @ParameterizedTest
  @EnumSource(Companies.class)
  @SneakyThrows
  public void interceptMeetingDeletion_quota_decremented_ok(Companies company) {

    final Counter counter = getCounterFromCompany(company);
    counter.increment();
    assertThat(counter.getOffset()).isEqualTo(1);
    when(user.getCompany()).thenReturn(company);
    when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{1l});
    when(meetingRepository.findById((Long) proceedingJoinPoint.getArgs()[0]))
        .thenReturn(Optional.of(reservation));

    quotaService.interceptDeleteOperation(proceedingJoinPoint);
    assertThat(counter.getOffset()).isEqualTo(0);
  }

  @ParameterizedTest
  @EnumSource(Companies.class)
  @SneakyThrows
  public void interceptMeetingDeletion_meetingAlreadyDeleted_quota_notDecremented_ok(
      Companies company) {

    final Counter counter = getCounterFromCompany(company);
    counter.increment();
    assertThat(counter.getOffset()).isEqualTo(1);
    when(user.getCompany()).thenReturn(company);
    when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{1l});
    when(meetingRepository.findById((Long) proceedingJoinPoint.getArgs()[0]))
        .thenReturn(Optional.empty());

    quotaService.interceptDeleteOperation(proceedingJoinPoint);
    assertThat(counter.getOffset()).isEqualTo(1);
  }

  private Counter getCounterFromCompany(Companies company) {
    return company.equals(Companies.COKE) ? quotaService.getCokeCounter() :
        quotaService.getPepsiCounter();
  }

}
