package com.sy.coladay.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sy.coladay.company.Companies;
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
 * ITest for {@link RoomRepository}
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
class RoomRepositoryIT {

  @Autowired
  RoomRepository underTest;

  @Test
  void saveNewRoom_withDuplicateName_throwException() {
    final Room room = new Room(null, "COKE_R01", Companies.COKE,null);

    assertThatThrownBy(() -> underTest.save(room))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @ParameterizedTest
  @MethodSource("invalidName")
  void saveNewRoom_withInvalidName_throwException(Room room) {

    assertConstraintViolationExceptionOnSaveRoom(room, "name cannot be null or empty");
  }

  static Stream<Arguments> invalidName() {
    return Stream.of(
        Arguments.of(new Room(null, null, Companies.COKE,null)),
        Arguments.of(new Room(null, "", Companies.COKE,null))
    );
  }

  @Test
  void saveNewRoom_withNullCompany_throwException() {
    final Room room = new Room(null, "room name", null,null);

    assertConstraintViolationExceptionOnSaveRoom(room, "company cannot be null");
  }

  @Test
  void saveNewRoom_ok() {
    final Room room = new Room(null, "room name", Companies.COKE,null);

    assertThat(underTest.save(room).getId()).isNotNull();
  }


  void assertConstraintViolationExceptionOnSaveRoom(Room room,
      String expectedMessage) {

    assertThatThrownBy(() -> underTest.save(room))
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
