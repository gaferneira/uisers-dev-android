package co.tuister.uisers.modules.tasks

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Task
import co.tuister.domain.usecases.tasks.GetMyTasksUseCase
import co.tuister.domain.usecases.tasks.RemoveTaskUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.InProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TasksViewModel(
    private val getMyTasks: GetMyTasksUseCase,
    private val removeUseCase: RemoveTaskUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class LoadItems(result: Result<List<Task>>) : State<List<Task>>(result)
        class RemoveItem(result: Result<Nothing>) : State<Nothing>(result)
    }

    fun initialize() {
        // No op
    }

    fun refresh() {
        updateTasks()
    }

    private fun updateTasks() {
        viewModelScope.launch {
            setState(State.LoadItems(InProgress()))
            val result = withContext(Dispatchers.IO) { getMyTasks() }
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

    fun remove(item: Task) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { removeUseCase(item) }
            result.fold(
                {
                    setState(State.RemoveItem(Result.Error(it)))
                },
                {
                    setState(State.RemoveItem(Result.Success()))
                }
            )
        }
    }
}
