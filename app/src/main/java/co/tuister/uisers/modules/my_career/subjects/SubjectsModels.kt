package co.tuister.uisers.modules.my_career.subjects

import co.tuister.domain.entities.Subject
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class SubjectsState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class LoadItems(result: Result<List<Subject>>) : SubjectsState<List<Subject>>(result)
    class LoadSemesterAverage(result: Result<Float>) : SubjectsState<Float>(result)
}
