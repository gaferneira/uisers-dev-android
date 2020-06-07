package co.tuister.uisers.modules.task_manager.add_task

import co.tuister.domain.entities.Task
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class AddTaskState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class Save(result: Result<Task>) : AddTaskState<Task>(result)
}
