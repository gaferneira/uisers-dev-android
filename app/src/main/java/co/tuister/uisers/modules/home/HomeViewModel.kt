package co.tuister.uisers.modules.home

import androidx.lifecycle.viewModelScope
import co.tuister.domain.usecases.my_career.GetCurrentSemesterUseCase
import co.tuister.domain.usecases.my_career.GetScheduleByDateUseCase
import co.tuister.domain.usecases.tasks.GetTasksByDateUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class HomeViewModel(
  private val getCurrentSemesterCase: GetCurrentSemesterUseCase,
  private val getTasksByDateUseCase: GetTasksByDateUseCase,
  private val scheduleByUseCase: GetScheduleByDateUseCase
) : BaseViewModel() {

    fun initialize() {
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
                val period = String.format("%d - %d", semester.year, semester.period)
                val header = HomeHeader("", period)
                setState(HomeState.LoadHeader(Result.Success(header)))
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
                setState(HomeState.LoadTasks(Result.Error(it)))
            }, {
                setState(HomeState.LoadTasks(Result.Success(it)))
            })
        }
    }

    private fun updateSubjects() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val result = withContext(Dispatchers.IO) { scheduleByUseCase.run(calendar.time) }
            result.fold({
                setState(HomeState.LoadSubjects(Result.Error(it)))
            }, {
                setState(HomeState.LoadSubjects(Result.Success(it)))
            })
        }
    }
}
