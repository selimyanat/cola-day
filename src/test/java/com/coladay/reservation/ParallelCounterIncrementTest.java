package com.coladay.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class to assert that the <code>Counter</code> increment operation is not subject to race
 * conditions.
 *
 * <p>Basically, the test down below gets executed in parallel by 4 threads and finally the
 * method marked with the annotation <code>After</code> is called and checks the current counter
 * value.
 * </p>
 *
 * @author selim
 */
@RunWith(ConcurrentTestRunner.class)
@Slf4j
public class ParallelCounterIncrementTest {

  private static final int COUNTER_LIMIT = 2;

  private Counter counter = new Counter(COUNTER_LIMIT);

  @Test
  public void increment_counter() {
    LOG.debug("Thread {} Attempt to increment counter...", Thread.currentThread().getName());
    counter.increment();
  }

  @After
  public void counter_value_should_be_equal_to_limit() {
    assertThat(counter.getOffset()).isEqualTo(COUNTER_LIMIT);
  }

}
