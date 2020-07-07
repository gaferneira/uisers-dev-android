package co.tuister.uisers.utils.extensions

import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.roundToInt

const val TEN = 10.0

fun Number.format(digits: Int = 2, fillZeros: Boolean = false): String {

    return if (fillZeros) {
        "%.${digits}f".format(this)
    } else {
        val pattern = "##.##"
        DecimalFormat(pattern).format(this)
    }
}

fun Float.round(scale: Int = 2): Float {
    return (this * TEN.pow(scale.toDouble())).roundToInt() / TEN.pow(scale.toDouble()).toFloat()
}
