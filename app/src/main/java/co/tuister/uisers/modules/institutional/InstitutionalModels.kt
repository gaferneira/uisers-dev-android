package co.tuister.uisers.modules.institutional

import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class InstitutionalState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class LoadItems(result: Result<List<InstitutionalMenu>>) :
        InstitutionalState<List<InstitutionalMenu>>(result)
}

data class InstitutionalMenu(val title: Int, val icon: Int, val backgroundColor: Int)
