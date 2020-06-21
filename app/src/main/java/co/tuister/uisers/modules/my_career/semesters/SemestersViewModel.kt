package co.tuister.uisers.modules.my_career.semesters

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Semester
import co.tuister.domain.usecases.my_career.ChangeCurrentSemesterUseCase
import co.tuister.domain.usecases.my_career.GetAllSemestersUseCase
import co.tuister.domain.usecases.my_career.SaveSemesterUseCase
import co.tuister.uisers.common.BaseState
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

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class LoadItems(result: Result<List<Semester>>) : State<List<Semester>>(result)
        class SaveSemester(result: Result<Nothing>) : State<Nothing>(result)
        class ChangeCurrentSemester(result: Result<Nothing>) : State<Nothing>(result)
    }

    fun initialize() {
        // No op
    }

    fun refresh() {
        updateSemesters()
    }

    private fun updateSemesters() {
        viewModelScope.launch {
            setState(
                State.LoadItems(Result.InProgress())
            )
            val result = withContext(Dispatchers.IO) { getAllSemesters.run() }
            result.fold(
                {
                    setState(State.LoadItems(Result.Error(it)))
                },
                {
                    setState(State.LoadItems(Result.Success(it)))
                }
            )
        }
    }

    fun saveSemester(semester: Semester) {
        setState(State.SaveSemester(Result.InProgress()))
        viewModelScope.launch {
            saveSemester.run(semester).fold(
                {
                    setState(State.SaveSemester(Result.Error(it)))
                },
                {
                    setState(State.SaveSemester(Result.Success()))
                }
            )
        }
    }

    fun changeCurrentSemester(semester: Semester) {
        setState(State.ChangeCurrentSemester(Result.InProgress()))
        viewModelScope.launch {
            changeCurrentSemester.run(semester).fold(
                {
                    setState(State.ChangeCurrentSemester(Result.Error(it)))
                },
                {
                    setState(State.ChangeCurrentSemester(Result.Success()))
                }
            )
        }
    }
}
