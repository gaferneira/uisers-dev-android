package co.tuister.uisers.modules.task_manager.add_task

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Task
import co.tuister.domain.usecases.tasks.SaveTaskUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.InProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTaskViewModel(
  private val saveTask: SaveTaskUseCase
) : BaseViewModel() {

    fun initialize() {
    }

    fun saveTask(task: Task) {
        setState(AddTaskState.Save(InProgress()))
        viewModelScope.launch {
            setState(AddTaskState.Save(InProgress()))
            val result = withContext(Dispatchers.IO) { saveTask.run(task) }
            result.fold({
                setState(AddTaskState.Save(Result.Error(it)))
            }, {
                setState(AddTaskState.Save(Result.Success(task)))
            })
        }
    }
}
