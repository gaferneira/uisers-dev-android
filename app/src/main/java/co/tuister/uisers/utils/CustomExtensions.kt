package co.tuister.uisers.utils

fun Number.format(digits: Int = 2) = "%.${digits}f".format(this)
