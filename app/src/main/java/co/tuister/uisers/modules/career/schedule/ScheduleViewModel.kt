package co.tuister.uisers.modules.career.schedule

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.entities.Subject
import co.tuister.domain.usecases.career.GetMySubjectsUseCase
import co.tuister.domain.usecases.career.GetScheduleUseCase
import co.tuister.domain.usecases.career.RemoveSchedulePeriodUseCase
import co.tuister.domain.usecases.career.SaveSchedulePeriodUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleViewModel(
    private val getMySubjects: GetMySubjectsUseCase,
    private val getSchedule: GetScheduleUseCase,
    private val savePeriodUseCase: SaveSchedulePeriodUseCase,
    private val removeUseCase: RemoveSchedulePeriodUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class LoadItems(result: Result<List<SchedulePeriod>>) : State<List<SchedulePeriod>>(result)
        class LoadSubjects(result: Result<List<Subject>>) : State<List<Subject>>(result)
        class SavePeriod(result: Result<Nothing>) : State<Nothing>(result)
        class RemoveItem(result: Result<Nothing>) : State<Nothing>(result)
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
            val result = withContext(Dispatchers.IO) { getSchedule() }
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
            val result = withContext(Dispatchers.IO) { getMySubjects() }
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
            savePeriodUseCase(period).fold(
                {
                    setState(State.SavePeriod(Result.Error(it)))
                },
                {
                    setState(State.SavePeriod(Result.Success()))
                }
            )
        }
    }

    fun removePeriod(item: SchedulePeriod) {
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
