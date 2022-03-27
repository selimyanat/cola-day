package com.sy.coladay.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Counter}.
 */
class CounterTest {

  Counter underTest;

  @Test
  void increment_quota_not_reached_returnTrue() {
    underTest = new Counter(0, 2);

    assertAll(
        () -> assertThat(underTest.increment()).isTrue(),
        () -> assertThat(underTest.getOffset()).isEqualTo(1)
    );
  }

  @Test
  void increment_quota_reached_returnFalse() {
    underTest = new Counter(0, 2);
    IntStream.of(0, 2).forEach(value -> underTest.increment());

    assertAll(
        () -> assertThat(underTest.increment()).isFalse(),
        () -> assertThat(underTest.getOffset()).isEqualTo(2)
    );
  }

  @Test
  void decrement_quota_offset_higherThanZero_returnTrue() {
    underTest = new Counter(1, 2);

    assertAll(
        () -> assertThat(underTest.decrement()).isTrue(),
        () -> assertThat(underTest.getOffset()).isZero()
    );
  }

  @Test
  void decrement_quota_offset_equalToZero_returnFalse() {
    underTest = new Counter(0, 2);

    assertAll(
        () -> assertThat(underTest.decrement()).isFalse(),
        () -> assertThat(underTest.getOffset()).isZero()
    );
  }

}
