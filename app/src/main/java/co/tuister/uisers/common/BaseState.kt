package co.tuister.uisers.common

import co.tuister.domain.base.Failure
import co.tuister.uisers.utils.ProgressType
import co.tuister.uisers.utils.Result

open class BaseState<out T : Any>(private val result: Result<T> = Result.Success()) {
    object Initial : BaseState<Any>()
    class Error(failure: Failure) : BaseState<Any>(Result.Error(failure))
    object InProgress : BaseState<Any>(Result.InProgress())

    // fun isFailure() = result is Result.Error
    // fun inProgress() = result is Result.InProgress
    // fun isSuccess() = result is Result.Success

    val data: T?
        get() = result.data

    fun handleResult(
        inProgress: ((ProgressType) -> Unit)? = null,
        onError: ((Failure?) -> Unit)? = null,
        onSuccess: (T?) -> Unit
    ) {

        when (result) {
            is Result.InProgress -> {
                inProgress?.invoke(result.type)
            }
            is Result.Error -> {
                onError?.invoke(result.exception)
            }
            is Result.Success -> {
                onSuccess.invoke(data)
            }
        }
    }
}
