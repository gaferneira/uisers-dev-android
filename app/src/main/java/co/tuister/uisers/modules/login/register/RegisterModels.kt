package co.tuister.uisers.modules.login.register

import co.tuister.domain.entities.User
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class RegisterState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class ValidateRegister(val result: Result<User>) : RegisterState<User>(result)
}
