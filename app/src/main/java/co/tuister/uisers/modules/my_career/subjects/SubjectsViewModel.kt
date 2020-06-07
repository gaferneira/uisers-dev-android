package co.tuister.uisers.modules.my_career.subjects

import androidx.lifecycle.viewModelScope
import co.tuister.domain.usecases.my_career.GetCurrentSemesterUseCase
import co.tuister.domain.usecases.my_career.GetMySubjectsUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectsViewModel(
  private val getMySubjects: GetMySubjectsUseCase,
  private val getCurrentSemester: GetCurrentSemesterUseCase
) : BaseViewModel() {

    fun initialize() {
    }

    fun refresh() {
        updateSubjects()
        updateAverage()
    }

    private fun updateSubjects() {
        viewModelScope.launch {
            setState(SubjectsState.LoadItems(Result.InProgress))
            val result = withContext(Dispatchers.IO) { getMySubjects.run() }
            result.fold({
                setState(
                    SubjectsState.LoadItems(Result.Error(it))
                )
            }, {
                setState(SubjectsState.LoadItems(Result.Success(it))
                )
            })
        }
    }

    private fun updateAverage() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { getCurrentSemester.run() }
            result.fold({
                setState(SubjectsState.LoadSemesterAverage(Result.Error(it)))
            }, {
                setState(
                    SubjectsState.LoadSemesterAverage(Result.Success(it.average))
                )
            })
        }
    }
}
