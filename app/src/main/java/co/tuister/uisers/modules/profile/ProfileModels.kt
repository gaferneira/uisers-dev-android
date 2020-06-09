package co.tuister.uisers.modules.profile

import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class ProfileState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class ValidateProfileUpdate(val result: Result<Boolean>) : ProfileState<Boolean>(result)
}
