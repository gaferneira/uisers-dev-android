package co.tuister.uisers.utils

import java.util.UUID

class StringUtils private constructor() {

    companion object {
        fun generateId() = UUID.randomUUID().toString()
    }
}
