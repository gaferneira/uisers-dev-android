package co.tuister.uisers.utils

import co.tuister.domain.base.Failure
import co.tuister.uisers.utils.ProgressType.FINAL

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val result: T? = null) : Result<T>()
    data class Error(val exception: Failure?) : Result<Nothing>()
    data class InProgress(val type: ProgressType = FINAL) : Result<Nothing>()

    val data: T?
        get() = when (this) {
            is Success -> result
            is Error -> null
            is InProgress -> null
        }
}

enum class ProgressType {
    DOWNLOADING,
    FINAL
}
