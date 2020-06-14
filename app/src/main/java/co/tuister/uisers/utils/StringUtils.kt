package co.tuister.uisers.utils

import java.util.UUID

class StringUtils {
    companion object {
        fun generateId() = UUID.randomUUID().toString()
    }
}

fun String.capitalizeFormat(): String {
    return this.toLowerCase().capitalize()
}
