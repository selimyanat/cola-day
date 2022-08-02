package com.sy.coladay.reservation

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner
import com.sy.coladay.reservation.ParallelCounterDecrementTest
import lombok.extern.slf4j.Slf4j
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal.ZERO

/**
 * Test class to assert that the `Counter` decrement operation is not subject to race
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
class ParallelCounterDecrementTest {

    var counter = Counter(COUNTER_LIMIT)

    @Test
    fun `thread increment counter safely`() {
        LOG.debug("Thread {} Attempt to decrement counter...", Thread.currentThread().name)
        counter.decrement()
    }

    @After
    fun `counter offset should be equal to zero`() {
        assertThat(counter.getOffset()).isEqualTo(ZERO.toInt())
    }

    companion object {
        const val COUNTER_LIMIT = 2
        val LOG: Logger = LoggerFactory.getLogger(ParallelCounterDecrementTest::class.java)
    }
}