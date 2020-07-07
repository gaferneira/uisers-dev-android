package co.tuister.uisers.utils.extensions

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NumberExtensionsTest {

    @Test fun `Check Number_format extension works as expected`() {

        val number1 = 2.9496837282

        assertThat(number1.format(0)).isEqualTo("3")
        assertThat(number1.format(1)).isEqualTo("2.9")
        assertThat(number1.format(2)).isEqualTo("2.95")
        assertThat(number1.format(3)).isEqualTo("2.95")
        assertThat(number1.format(3, fillZeros = true)).isEqualTo("2.950")

        val number2 = 2
        assertThat(number2.format(0)).isEqualTo("2")
        assertThat(number2.format(1)).isEqualTo("2")
        assertThat(number2.format(2)).isEqualTo("2")
        assertThat(number2.format(2, fillZeros = true)).isEqualTo("2.00")
    }
}
