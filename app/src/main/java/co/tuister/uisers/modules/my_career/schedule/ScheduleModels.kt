package co.tuister.uisers.modules.my_career.schedule

import co.tuister.domain.entities.SubjectClass
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class ScheduleState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class LoadItems(result: Result<List<SubjectClass>>) : ScheduleState<List<SubjectClass>>(result)
}
