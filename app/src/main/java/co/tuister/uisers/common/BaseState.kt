package co.tuister.uisers.common

import co.tuister.domain.base.Failure
import co.tuister.uisers.utils.ProgressType
import co.tuister.uisers.utils.Result

open class BaseState<out T : Any>(private val _result: Result<T> = Result.Success()) {
    object Initial : BaseState<Any>()
    class Error(failure: Failure) : BaseState<Any>(Result.Error(failure))
    object InProgress : BaseState<Any>(Result.InProgress())

    // fun isFailure() = _result is Result.Error
    // fun inProgress() = _result is Result.InProgress
    // fun isSuccess() = _result is Result.Success

    val data: T?
        get() = _result.data

    fun handleResult(
        inProgress: ((ProgressType) -> Unit)? = null,
        onError: ((Failure?) -> Unit)? = null,
        onSuccess: (T?) -> Unit
    ) {

        when (_result) {
            is Result.InProgress -> {
                inProgress?.invoke(_result.type)
            }
            is Result.Error -> {
                onError?.invoke(_result.exception)
            }
            is Result.Success -> {
                onSuccess.invoke(data)
            }
        }
    }
}
