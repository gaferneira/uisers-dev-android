package co.tuister.uisers.modules.my_career.add_subject

import co.tuister.domain.entities.Subject
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class AddSubjectsState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class Save(result: Result<Subject>) : AddSubjectsState<Subject>(result)
    class LoadDefaultSubjects(result: Result<List<Subject>>) :
        AddSubjectsState<List<Subject>>(result)
}
