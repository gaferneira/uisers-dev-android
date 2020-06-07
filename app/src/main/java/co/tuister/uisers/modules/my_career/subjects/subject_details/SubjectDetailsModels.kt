package co.tuister.uisers.modules.my_career.subjects.subject_details

import co.tuister.domain.entities.Note
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class SubjectDetailsState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class LoadItems(result: Result<List<Note>>) : SubjectDetailsState<List<Note>>(result)
    class SaveNote(result: Result<Nothing>) : SubjectDetailsState<Nothing>(result)
}
