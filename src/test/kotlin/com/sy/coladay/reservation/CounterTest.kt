package com.sy.coladay.reservation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.util.stream.IntStream

/**
 * Test class for [Counter].
 */
internal class CounterTest {

    var underTest: Counter? = null

    @Test
    fun `increment counter when quota is not_reached return true` () {
        underTest = Counter(0, 2)

        assertAll(
            Executable { assertThat(underTest!!.increment()).isTrue()},
            Executable { assertThat(underTest!!.getOffset()).isEqualTo(1) }
        )
    }

    @Test
    fun `increment counter when quota is reached return false`() {
        underTest = Counter(0, 2)
        IntStream.of(0, 2).forEach { underTest!!.increment() }

        assertAll(
            Executable { assertThat(underTest!!.increment()).isFalse() },
            Executable { assertThat(underTest!!.getOffset()).isEqualTo(2) }
        )
    }

    @Test
    fun `decrement counter when quota is higher than zero return true`() {
        underTest = Counter(1, 2)

        assertAll(
            Executable { assertThat(underTest!!.decrement()).isTrue() },
            Executable { assertThat(underTest!!.getOffset()).isZero() }
        )
    }

    @Test
    fun `decrement counter when quota is zero return false` () {
        underTest = Counter(0, 2)

        assertAll(
            Executable { assertThat(underTest!!.decrement()).isFalse()},
            Executable { assertThat(underTest!!.getOffset()).isZero()}
        )
    }
}