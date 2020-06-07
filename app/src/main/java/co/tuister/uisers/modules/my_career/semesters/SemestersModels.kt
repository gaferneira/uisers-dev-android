package co.tuister.uisers.modules.my_career.semesters

import co.tuister.domain.entities.Semester
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class SemestersState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class LoadItems(result: Result<List<Semester>>) : SemestersState<List<Semester>>(result)
    class SaveSemester(result: Result<Nothing>) : SemestersState<Nothing>(result)
    class ChangeCurrentSemester(result: Result<Nothing>) : SemestersState<Nothing>(result)
}
