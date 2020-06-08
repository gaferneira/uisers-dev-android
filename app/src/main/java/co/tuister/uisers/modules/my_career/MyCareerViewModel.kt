package co.tuister.uisers.modules.my_career

import androidx.lifecycle.viewModelScope
import co.tuister.domain.usecases.my_career.GetMySubjectsUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyCareerViewModel(
  private val getMySubjects: GetMySubjectsUseCase
) : BaseViewModel() {

    fun initialize() {
    }

    fun refresh() {
        updateSubjects()
    }

    fun updateSubjects() {
        viewModelScope.launch {
            setState(MyCareerState.LoadSubjects(Result.InProgress()))
            val result = withContext(Dispatchers.IO) { getMySubjects.run() }
            result.fold({
                setState(
                    MyCareerState.LoadSubjects(Result.Error(it))
                )
            }, {
                setState(
                    MyCareerState.LoadSubjects(Result.Success(it))
                )
            })
        }
    }
}
