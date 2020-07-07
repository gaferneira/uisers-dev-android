package co.tuister.uisers.modules.career.subjects

import androidx.lifecycle.viewModelScope
import co.tuister.domain.base.Either
import co.tuister.domain.entities.Subject
import co.tuister.domain.usecases.career.GetCurrentSemesterUseCase
import co.tuister.domain.usecases.career.GetMySubjectsUseCase
import co.tuister.domain.usecases.career.RemoveSubjectUseCase
import co.tuister.domain.usecases.career.SaveSemesterUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.extensions.round
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectsViewModel(
    private val getMySubjects: GetMySubjectsUseCase,
    private val getCurrentSemester: GetCurrentSemesterUseCase,
    private val updateSemester: SaveSemesterUseCase,
    private val removeSubject: RemoveSubjectUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class LoadSubjects(result: Result<List<Subject>>) : State<List<Subject>>(result)
        class LoadSemesterAverage(result: Result<Float>) : State<Float>(result)
        class RemoveSubject(result: Result<Nothing>) : State<Nothing>(result)
    }

    fun initialize() {
        // No op
    }

    fun refresh() {
        updateSubjects()
    }

    private fun updateSubjects() {
        viewModelScope.launch {
            setState(State.LoadSubjects(Result.InProgress()))
            val result = withContext(Dispatchers.IO) { getMySubjects.run() }
            result.fold(
                {
                    setState(State.LoadSubjects(Result.Error(it)))
                },
                {
                    setState(State.LoadSubjects(Result.Success(it)))
                    val average = calculateAverage(it)
                    setState(State.LoadSemesterAverage(Result.Success(average?.second)))
                    getCurrentSemester(average)
                }
            )
        }
    }

    private fun getCurrentSemester(newAverage: Pair<Int, Float>?) {
        viewModelScope.launch {
            when (val result = withContext(Dispatchers.IO) { getCurrentSemester.run() }) {
                is Either.Right -> {
                    val semester = result.value
                    val oldAverage = semester.average
                    val oldCredits = semester.credits
                    if (newAverage != null && (newAverage.first != oldCredits || newAverage.second != oldAverage)) {
                        semester.credits = newAverage.first
                        semester.average = newAverage.second
                        updateSemester.run(semester)
                    }
                }
            }
        }
    }

    private fun calculateAverage(data: List<Subject>?): Pair<Int, Float>? {
        return data?.takeIf { it.isNotEmpty() }?.let { subjects ->
            val total = subjects.map { it.credits * it.note }.sum()
            val totalCredits = subjects.sumBy { it.credits }
            val average = if (totalCredits != 0) {
                total / totalCredits
            } else {
                0f
            }
            Pair(totalCredits, average.round())
        }
    }

    fun removeSubject(subject: Subject) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { removeSubject.run(subject) }
            result.fold(
                {
                    setState(State.RemoveSubject(Result.Error(it)))
                },
                {
                    setState(State.RemoveSubject(Result.Success()))
                }
            )
        }
    }
}
