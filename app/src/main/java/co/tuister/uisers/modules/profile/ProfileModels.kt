package co.tuister.uisers.modules.profile

import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class ProfileState(result: Result<Any>) : BaseState<Any>(result)
