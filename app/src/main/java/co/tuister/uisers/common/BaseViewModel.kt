package co.tuister.uisers.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseViewModel : ViewModel() {

    val state: MutableStateFlow<BaseState<Any>> by lazy {
        MutableStateFlow<BaseState<Any>>(BaseState.Initial)
    }

    fun setState(baseState: BaseState<Any>) {
        state.value = baseState
    }
}
