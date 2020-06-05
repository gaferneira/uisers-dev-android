package co.tuister.uisers.modules.main

import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class MainState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class ValidateLogout(val result: Result<Boolean>) : MainState<Boolean>(result)
}

sealed class MainEvent {
    object GoToLogin : MainEvent()
}
