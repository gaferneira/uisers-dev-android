package co.tuister.uisers.modules.my_career

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Subject
import co.tuister.domain.usecases.my_career.GetMySubjectsUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyCareerViewModel(
  private val getMySubjects: GetMySubjectsUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class LoadSubjects(result: Result<List<Subject>>) : State<List<Subject>>(result)
    }

    fun initialize() {
        // No op
    }

    fun refresh() {
        updateSubjects()
    }

    fun updateSubjects() {
        viewModelScope.launch {
            setState(State.LoadSubjects(Result.InProgress()))
            val result = withContext(Dispatchers.IO) { getMySubjects.run() }
            result.fold({
                setState(
                    State.LoadSubjects(Result.Error(it))
                )
            }, {
                setState(
                    State.LoadSubjects(Result.Success(it))
                )
            })
        }
    }
}
