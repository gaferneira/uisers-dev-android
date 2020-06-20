package co.tuister.uisers.utils

import java.text.DecimalFormat

fun Number.format(digits: Int = 2, fillZeros: Boolean = false): String {

    return if (fillZeros) {
        "%.${digits}f".format(this)
    } else {
        val pattern = "##.##"
        DecimalFormat(pattern).format(this)
    }
}
