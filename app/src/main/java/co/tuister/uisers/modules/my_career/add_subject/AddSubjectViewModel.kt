package co.tuister.uisers.modules.my_career.add_subject

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Subject
import co.tuister.domain.usecases.my_career.GetAllSubjectsUseCase
import co.tuister.domain.usecases.my_career.SaveSubjectUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddSubjectViewModel(
    private val getAllSubject: GetAllSubjectsUseCase,
    private val saveSubject: SaveSubjectUseCase
) : BaseViewModel() {

    fun initialize() {
        getDefaultsSubjects()
    }

    private fun getDefaultsSubjects() {
        viewModelScope.launch {
            setState(AddSubjectsState.LoadDefaultSubjects(Result.InProgress))
            val result = withContext(Dispatchers.IO) { getAllSubject.run() }
            result.fold({
                setState(AddSubjectsState.LoadDefaultSubjects(Result.Error(it)))
            }, {
                setState(AddSubjectsState.LoadDefaultSubjects(Result.Success(it)))
            })
        }
    }

    fun saveSubject(subject: Subject) {
        viewModelScope.launch {
            setState(AddSubjectsState.Save(Result.InProgress))
            val result = withContext(Dispatchers.IO) { saveSubject.run(subject) }
            result.fold({
                setState(AddSubjectsState.Save(Result.Error(it)))
            }, {
                setState(AddSubjectsState.Save(Result.Success(subject)))
            })
        }
    }
}
