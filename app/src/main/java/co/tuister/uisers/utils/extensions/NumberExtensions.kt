package co.tuister.uisers.utils.extensions

import java.lang.StringBuilder
import java.text.DecimalFormat

fun Number.format(digits: Int = 2, fillZeros: Boolean = false): String {

    return if (fillZeros) {
        "%.${digits}f".format(this.toFloat())
    } else {
        val pattern = StringBuilder("##")
        if (digits > 0) {
            pattern.append(".")
            for (i in 0 until digits) {
                pattern.append("#")
            }
        }
        DecimalFormat(pattern.toString()).format(this)
    }
}
