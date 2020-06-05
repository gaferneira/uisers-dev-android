package co.tuister.uisers.modules.login

import co.tuister.domain.entities.User
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class LoginState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class ValidateLogin(val result: Result<User>) : LoginState<User>(result)
}

sealed class LoginEvent {
    class GoToMain(val user: User) : LoginEvent()
    object GoToLogin : LoginEvent()
}
