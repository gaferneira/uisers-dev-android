package co.tuister.uisers.utils

import co.tuister.domain.base.Failure
import co.tuister.uisers.utils.PROGESS_TYPE.FINAL

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val result: T? = null) : Result<T>()
    data class Error(val exception: Failure?) : Result<Nothing>()
    data class InProgress(val type: PROGESS_TYPE = FINAL) : Result<Nothing>()

    val data: T?
        get() = when (this) {
            is Success -> result
            is Error -> null
            is InProgress -> null
        }
}

enum class PROGESS_TYPE {
    DOWNLOADING,
    FINAL
}
