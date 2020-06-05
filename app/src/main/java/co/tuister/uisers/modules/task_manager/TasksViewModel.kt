package co.tuister.uisers.modules.task_manager

import androidx.lifecycle.viewModelScope
import co.tuister.domain.usecases.tasks.GetMyTasksUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TasksViewModel(
    private val getMyTasks: GetMyTasksUseCase
) : BaseViewModel() {

    fun initialize() {
    }

    fun refresh() {
        updateTasks()
    }

    private fun updateTasks() {
        viewModelScope.launch {
            setState(TasksState.LoadItems(Result.InProgress))
            val result = withContext(Dispatchers.IO) { getMyTasks.run() }
            result.fold({
                setState(TasksState.LoadItems(Result.Error(it)))
            }, {
                setState(TasksState.LoadItems(Result.Success(it)))
            })
        }
    }
}
