package com.coladay.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coladay.company.Companies;
import com.coladay.room.RoomRepository;
import com.coladay.user.UserRepository;
import java.util.HashSet;
import java.util.stream.Stream;
import javax.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * ITest class for {@link ReservationRepository}
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ReservationRepositoryIT {

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void saveNewReservation_withNullTimeSlot_ok() {
    final Reservation reservation = new Reservation(null, null,
        roomRepository.findById(10l).get(),
        userRepository.findById(1l).get());

    assertConstraintViolationExceptionOnsaveNewReservation(reservation, "time slot cannot be null");
  }

  @Test
  public void saveNewReservation_withNullRoom_ok() {
    final Reservation reservation = new Reservation(null, TimeSlots.EIGHT_AM_TO_NINE_AM,
        null,
        userRepository.findById(1l).get());

    assertConstraintViolationExceptionOnsaveNewReservation(reservation, "room cannot be null");
  }

  @Test
  public void saveNewReservation_withNullUser_ok() {
    final Reservation reservation = new Reservation(null, TimeSlots.EIGHT_AM_TO_NINE_AM,
        roomRepository.findById(10l).get(),
        null);

    assertConstraintViolationExceptionOnsaveNewReservation(reservation, "organizer cannot be null");
  }

  @Test
  public void saveNewReservation_onBusySchedule_throwException() {
    final Reservation reservation = new Reservation(null, TimeSlots.EIGHT_AM_TO_NINE_AM,
        roomRepository.findById(1l).get(),
        userRepository.findById(1l).get());

    assertThatThrownBy(() -> reservationRepository.save(reservation))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  public void saveNewReservation_ok() {
    final Reservation reservation = new Reservation(null, TimeSlots.EIGHT_AM_TO_NINE_AM,
        roomRepository.findById(10l).get(),
        userRepository.findById(2l).get());

    assertThat(reservationRepository.save(reservation).getId()).isNotNull();
  }

  @ParameterizedTest
  @MethodSource("countByOrganizerCompany_dataProvider")
  public void countByOrganizerCompany_returnsReservationCount(Companies company,
      int expectedCount) {
    assertThat(reservationRepository.countByOrganizerCompany(company)).isEqualTo(expectedCount);
  }

  private static Stream<Arguments> countByOrganizerCompany_dataProvider() {
    return Stream.of(
        Arguments.of(Companies.COKE, 1),
        Arguments.of(Companies.PEPSI, 0)
    );
  }

  private void assertConstraintViolationExceptionOnsaveNewReservation(Reservation reservation,
      String expectedMessage) {

    assertThatThrownBy(() -> reservationRepository.save(reservation))
        .isInstanceOf(ConstraintViolationException.class)
        .hasFieldOrProperty("constraintViolations")
        .extracting("constraintViolations")
        .flatExtracting(set -> ((HashSet<String>) set))
        .element(0)
        .isNotNull()
        .isInstanceOf(ConstraintViolationImpl.class)
        .hasFieldOrPropertyWithValue("messageTemplate", expectedMessage);
  }

}
