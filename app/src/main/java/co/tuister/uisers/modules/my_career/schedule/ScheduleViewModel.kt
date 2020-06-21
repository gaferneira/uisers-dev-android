package co.tuister.uisers.modules.my_career.schedule

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.entities.Subject
import co.tuister.domain.usecases.my_career.GetMySubjectsUseCase
import co.tuister.domain.usecases.my_career.GetScheduleUseCase
import co.tuister.domain.usecases.my_career.SaveSchedulePeriodUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleViewModel(
    private val getMySubjects: GetMySubjectsUseCase,
    private val getSchedule: GetScheduleUseCase,
    private val savePeriod: SaveSchedulePeriodUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class LoadItems(result: Result<List<SchedulePeriod>>) : State<List<SchedulePeriod>>(result)
        class LoadSubjects(result: Result<List<Subject>>) : State<List<Subject>>(result)
        class SavePeriod(result: Result<Nothing>) : State<Nothing>(result)
    }

    fun initialize() {
        // no op
    }

    fun refresh() {
        updateClasses()
        updateSubjects()
    }

    private fun updateClasses() {
        viewModelScope.launch {
            setState(
                State.LoadItems(Result.InProgress())
            )
            val result = withContext(Dispatchers.IO) { getSchedule.run() }
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

    private fun updateSubjects() {
        viewModelScope.launch {
            setState(State.LoadSubjects(Result.InProgress()))
            val result = withContext(Dispatchers.IO) { getMySubjects.run() }
            result.fold(
                {
                    setState(State.LoadSubjects(Result.Error(it)))
                },
                {
                    setState(State.LoadSubjects(Result.Success(it)))
                }
            )
        }
    }

    fun savePeriod(period: SchedulePeriod) {
        setState(State.SavePeriod(Result.InProgress()))
        viewModelScope.launch {
            savePeriod.run(period).fold(
                {
                    setState(State.SavePeriod(Result.Error(it)))
                },
                {
                    setState(State.SavePeriod(Result.Success()))
                }
            )
        }
    }
}
