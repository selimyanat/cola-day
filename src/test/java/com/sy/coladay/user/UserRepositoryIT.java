package com.sy.coladay.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sy.coladay.company.Companies;
import java.util.HashSet;
import java.util.Optional;
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
 * ITest class for {@link UserRepository}
 * @author selim
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryIT {

  @Autowired
  UserRepository underTest;

  @Test
  void saveNewUser_withDuplicateName_throwException() {
    final User user = new User(null, "user1", "password1", Companies.PEPSI, Role.USER);

    assertThatThrownBy(() -> underTest.save(user))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @ParameterizedTest
  @MethodSource("invalidUserName")
  void saveNewUser_withInvalidName_throwException(User user) {

    assertConstraintViolationExceptionOnsaveNewUser(user, "name cannot be null or empty");
  }

  static Stream<Arguments> invalidUserName() {
    return Stream.of(
        Arguments.of(new User(null, null, "password1", Companies.COKE, Role.USER)),
        Arguments.of(new User(null, "", "password1", Companies.COKE, Role.USER))
    );
  }

  @ParameterizedTest
  @MethodSource("invalidPassword")
  void saveNewUser_withInvalidPassword_throwException(User user) {

    assertConstraintViolationExceptionOnsaveNewUser(user, "password cannot be null or empty");
  }

  static Stream<Arguments> invalidPassword() {
    return Stream.of(
        Arguments.of(new User(null, "user", "", Companies.PEPSI, Role.USER)),
        Arguments.of(new User(null, "user", null, Companies.PEPSI, Role.USER))
    );
  }

  @Test
  void saveNewUser_withNullCompany_throwException() {
    final User user = new User(null, "user", "password", null, Role.USER);

    assertConstraintViolationExceptionOnsaveNewUser(user, "company cannot be null");
  }

  @Test
  void saveNewUser_withNullRole_throwException() {
    final User user = new User(null, "user", "password", Companies.COKE, null);

    assertConstraintViolationExceptionOnsaveNewUser(user, "role cannot be null");
  }

  @Test
  void saveNewUser_ok() {
    final User user = new User(null, "user10", "password10", Companies.PEPSI, Role.USER);

    assertThat(underTest.save(user).getId()).isNotNull();
  }

  @Test
  void findByName_existingUser_returnUser_ok() {
    final Optional<User> user = underTest.findByName("user1");

    assertAll(
        () -> assertThat(user.isPresent()).isTrue(),
        () -> assertThat(user.get().getId()).isEqualTo(1l),
        () -> assertThat(user.get().getName()).isEqualTo("user1"),
        () -> assertThat(user.get().getPassword()).isEqualTo("password1"),
        () -> assertThat(user.get().getCompany()).isEqualTo(Companies.COKE));
  }

  @Test
  void findByName_unKnownUser_returnOptionalEmpty_ok() {
    assertThat(underTest.findByName("unknownUser").isPresent()).isFalse();
  }


  void assertConstraintViolationExceptionOnsaveNewUser(User user,
      String expectedMessage) {

    assertThatThrownBy(() -> underTest.save(user))
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