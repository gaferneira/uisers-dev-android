package co.tuister.uisers.modules.login.forgot_password

import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class ForgotPasswordState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class ValidateEmail(val result: Result<Nothing>) : ForgotPasswordState<Nothing>(result)
}
