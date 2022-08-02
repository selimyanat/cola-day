package com.sy.coladay.reservation

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner
import lombok.extern.slf4j.Slf4j
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Test class to assert that the `Counter` increment operation is not subject to race
 * conditions.
 *
 *
 * Basically, the test down below gets executed in parallel by 4 threads and finally the
 * method marked with the annotation `After` is called and checks the current counter
 * value.
 *
 *
 * @author selim
 */
@RunWith(ConcurrentTestRunner::class)
@Slf4j
class ParallelCounterIncrementTest {
    var counter = Counter(COUNTER_LIMIT)
    @Test
    fun `thread increment counter safely`() {
        LOG.debug("Thread {} Attempt to increment counter...", Thread.currentThread().name)
        counter.increment()
    }

    @After
    fun `counter offset should be equal to counter limit`() {
        Assertions.assertThat(counter.getOffset()).isEqualTo(COUNTER_LIMIT)
    }

    companion object {
        const val COUNTER_LIMIT = 2
        val LOG: Logger = LoggerFactory.getLogger(ParallelCounterIncrementTest::class.java)

    }
}