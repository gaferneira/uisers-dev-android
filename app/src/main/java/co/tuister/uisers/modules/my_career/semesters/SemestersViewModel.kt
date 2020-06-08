package co.tuister.uisers.modules.my_career.semesters

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Semester
import co.tuister.domain.usecases.my_career.ChangeCurrentSemesterUseCase
import co.tuister.domain.usecases.my_career.GetAllSemestersUseCase
import co.tuister.domain.usecases.my_career.SaveSemesterUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SemestersViewModel(
  private val getAllSemesters: GetAllSemestersUseCase,
  private val saveSemester: SaveSemesterUseCase,
  private val changeCurrentSemester: ChangeCurrentSemesterUseCase
) : BaseViewModel() {

    fun initialize() {
    }

    fun refresh() {
        updateSemesters()
    }

    private fun updateSemesters() {
        viewModelScope.launch {
            setState(
                SemestersState.LoadItems(Result.InProgress())
            )
            val result = withContext(Dispatchers.IO) { getAllSemesters.run() }
            result.fold({
                setState(SemestersState.LoadItems(Result.Error(it)))
            }, {
                setState(SemestersState.LoadItems(Result.Success(it)))
            })
        }
    }

    fun saveSemester(semester: Semester) {
        setState(SemestersState.SaveSemester(Result.InProgress()))
        viewModelScope.launch {
            saveSemester.run(semester).fold({
                setState(SemestersState.SaveSemester(Result.Error(it)))
            }, {
                setState(SemestersState.SaveSemester(Result.Success()))
            })
        }
    }

    fun changeCurrentSemester(semester: Semester) {
        setState(SemestersState.ChangeCurrentSemester(Result.InProgress()))
        viewModelScope.launch {
            changeCurrentSemester.run(semester).fold({
                setState(SemestersState.ChangeCurrentSemester(Result.Error(it)))
            }, {
                setState(SemestersState.ChangeCurrentSemester(Result.Success()))
            })
        }
    }
}
