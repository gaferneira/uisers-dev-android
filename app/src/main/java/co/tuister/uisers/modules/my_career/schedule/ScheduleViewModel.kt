package co.tuister.uisers.modules.my_career.schedule

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.usecases.my_career.GetScheduleUseCase
import co.tuister.domain.usecases.my_career.SaveSchedulePeriodUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleViewModel(
  private val getSchedule: GetScheduleUseCase,
  private val savePeriod: SaveSchedulePeriodUseCase
) : BaseViewModel() {

    fun initialize() {
    }

    fun refresh() {
        updateClasses()
    }

    private fun updateClasses() {
        viewModelScope.launch {
            setState(
                ScheduleState.LoadItems(Result.InProgress)
            )
            val result = withContext(Dispatchers.IO) { getSchedule.run() }
            result.fold({
                setState(ScheduleState.LoadItems(Result.Error(it)))
            }, {
                setState(ScheduleState.LoadItems(Result.Success(it)))
            })
        }
    }

    fun savePeriod(period: SchedulePeriod) {
        setState(ScheduleState.SavePeriod(Result.InProgress))
        viewModelScope.launch {
            savePeriod.run(period).fold({
                setState(ScheduleState.SavePeriod(Result.Error(it)))
            }, {
                setState(ScheduleState.SavePeriod(Result.Success()))
            })
        }
    }
}
