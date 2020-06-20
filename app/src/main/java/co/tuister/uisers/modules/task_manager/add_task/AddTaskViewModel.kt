package co.tuister.uisers.modules.task_manager.add_task

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Task
import co.tuister.domain.usecases.tasks.SaveTaskUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.InProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTaskViewModel(
    private val saveTask: SaveTaskUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class Save(result: Result<Task>) : State<Task>(result)
    }

    fun initialize() {
        // No op
    }

    fun saveTask(task: Task) {
        setState(State.Save(InProgress()))
        viewModelScope.launch {
            setState(State.Save(InProgress()))
            val result = withContext(Dispatchers.IO) { saveTask.run(task) }
            result.fold(
                {
                    setState(State.Save(Result.Error(it)))
                },
                {
                    setState(State.Save(Result.Success(task)))
                }
            )
        }
    }
}
