package com.coladay.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Counter}
 */
public class CounterTest {

  private Counter counter;

  @Test
  public void increment_quota_not_reached_returnTrue() {
    counter = new Counter(0, 2);

    assertThat(counter.increment()).isTrue();
    assertThat(counter.getOffset()).isEqualTo(1);
  }

  @Test
  public void increment_quota_reached_returnFalse() {
    counter = new Counter(0, 2);
    IntStream.of(0, 2).forEach(value -> counter.increment());

    assertThat(counter.increment()).isFalse();
    assertThat(counter.getOffset()).isEqualTo(2);
  }

  @Test
  public void decrement_quota_offset_higherThanZero_returnTrue() {
    counter = new Counter(1, 2);

    assertThat(counter.decrement()).isTrue();
    assertThat(counter.getOffset()).isEqualTo(0);
  }

  @Test
  public void decrement_quota_offset_equalToZero_returnFalse() {
    counter = new Counter(0, 2);

    assertThat(counter.decrement()).isFalse();
    assertThat(counter.getOffset()).isEqualTo(0);
  }

}
