package co.tuister.uisers.modules.my_career.subjects

import androidx.lifecycle.viewModelScope
import co.tuister.domain.usecases.my_career.GetCurrentSemesterUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectsViewModel(
  private val getCurrentSemester: GetCurrentSemesterUseCase
) : BaseViewModel() {

    fun initialize() {
    }

    fun refresh() {
        updateAverage()
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
