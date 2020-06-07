package co.tuister.uisers.modules.my_career.schedule

import co.tuister.domain.entities.SchedulePeriod
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class ScheduleState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class LoadItems(result: Result<List<SchedulePeriod>>) : ScheduleState<List<SchedulePeriod>>(result)
    class SavePeriod(result: Result<Nothing>) : ScheduleState<Nothing>(result)
}
