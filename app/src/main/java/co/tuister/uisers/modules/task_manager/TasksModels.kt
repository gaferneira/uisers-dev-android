package co.tuister.uisers.modules.task_manager

import co.tuister.domain.entities.Task
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class TasksState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class LoadItems(result: Result<List<Task>>) : TasksState<List<Task>>(result)
}
