package co.tuister.uisers.modules.home

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.entities.Task
import co.tuister.domain.usecases.my_career.GetCurrentSemesterUseCase
import co.tuister.domain.usecases.my_career.GetScheduleByDateUseCase
import co.tuister.domain.usecases.tasks.GetTasksByDateUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
  private val getCurrentSemesterCase: GetCurrentSemesterUseCase,
  private val getTasksByDateUseCase: GetTasksByDateUseCase,
  private val scheduleByUseCase: GetScheduleByDateUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class LoadHeader(result: Result<HomeHeader>) : State<HomeHeader>(result)
        class LoadTasks(result: Result<List<Task>>) : State<List<Task>>(result)
        class LoadSubjects(result: Result<List<SchedulePeriod>>) : State<List<SchedulePeriod>>(result)
    }

    fun initialize() {
        // No op
    }

    fun refresh() {
        updateLabels()
        updateTasks()
        updateSubjects()
    }

    private fun updateLabels() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { getCurrentSemesterCase.run() }
            result.fold({
                // left --> error
            }, { semester ->
                val period = semester.period
                val header = HomeHeader("", period)
                setState(State.LoadHeader(Result.Success(header)))
            })
        }
    }

    private fun updateTasks() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val result = withContext(Dispatchers.IO) {
                getTasksByDateUseCase.run(calendar.time)
            }
            result.fold({
                setState(State.LoadTasks(Result.Error(it)))
            }, {
                setState(State.LoadTasks(Result.Success(it)))
            })
        }
    }

    private fun updateSubjects() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val result = withContext(Dispatchers.IO) { scheduleByUseCase.run(calendar.time) }
            result.fold({
                setState(State.LoadSubjects(Result.Error(it)))
            }, {
                setState(State.LoadSubjects(Result.Success(it)))
            })
        }
    }
}
