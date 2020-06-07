package co.tuister.uisers.modules.my_career

import co.tuister.domain.entities.Subject
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class MyCareerState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class LoadSubjects(result: Result<List<Subject>>) : MyCareerState<List<Subject>>(result)
}
