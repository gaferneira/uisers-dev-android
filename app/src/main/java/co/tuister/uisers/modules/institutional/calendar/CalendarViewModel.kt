package co.tuister.uisers.modules.institutional.calendar

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Event
import co.tuister.domain.usecases.institutional.GetEventsUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalendarViewModel(
    private val getEvents: GetEventsUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class LoadItems(result: Result<List<Event>>) : State<List<Event>>(result)
    }

    fun initialize() {
        // No op
    }

    fun refresh() {
        updateEvents()
    }

    private fun updateEvents() {
        viewModelScope.launch {
            setState(State.LoadItems(Result.InProgress()))
            val result = withContext(Dispatchers.IO) { getEvents.run() }
            result.fold(
                {
                    setState(State.LoadItems(Result.Error(it)))
                },
                {
                    setState(State.LoadItems(Result.Success(it)))
                }
            )
        }
    }
}
