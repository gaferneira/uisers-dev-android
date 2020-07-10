package co.tuister.uisers.modules.career.subjects.add

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.CareerSubject
import co.tuister.domain.entities.Subject
import co.tuister.domain.usecases.career.GetAllSubjectsUseCase
import co.tuister.domain.usecases.career.SaveSubjectUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.InProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddSubjectViewModel(
    private val getAllSubject: GetAllSubjectsUseCase,
    private val saveSubjectUseCase: SaveSubjectUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class Save(result: Result<Subject>) : State<Subject>(result)
        class LoadDefaultSubjects(result: Result<List<CareerSubject>>) :
            State<List<CareerSubject>>(result)
    }

    fun initialize() {
        getDefaultsSubjects()
    }

    private fun getDefaultsSubjects() {
        viewModelScope.launch {
            setState(State.LoadDefaultSubjects(InProgress()))
            val result = withContext(Dispatchers.IO) { getAllSubject() }
            result.fold(
                {
                    setState(State.LoadDefaultSubjects(Result.Error(it)))
                },
                {
                    setState(State.LoadDefaultSubjects(Result.Success(it)))
                }
            )
        }
    }

    fun saveSubject(subject: Subject) {
        setState(State.Save(InProgress()))
        viewModelScope.launch {
            setState(State.Save(InProgress()))
            val result = withContext(Dispatchers.IO) { saveSubjectUseCase(subject) }
            result.fold(
                {
                    setState(State.Save(Result.Error(it)))
                },
                {
                    setState(State.Save(Result.Success(subject)))
                }
            )
        }
    }
}
