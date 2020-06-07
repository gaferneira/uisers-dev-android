package co.tuister.uisers.modules.my_career.schedule

import androidx.lifecycle.viewModelScope
import co.tuister.domain.usecases.my_career.GetScheduleUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleViewModel(
  private val getSchedule: GetScheduleUseCase
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
}
