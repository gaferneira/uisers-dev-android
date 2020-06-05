package co.tuister.uisers.modules.my_career.subject_details

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Subject
import co.tuister.domain.usecases.my_career.GetNotesUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.modules.my_career.SubjectsState
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectDetailsViewModel(
    private val getNotes: GetNotesUseCase
) : BaseViewModel() {

    lateinit var subject: Subject

    fun initialize(subject: Subject) {
        this.subject = subject
    }

    fun refresh() {
        updateNotes()
    }

    private fun updateNotes() {
        viewModelScope.launch {
            setState(SubjectsState.LoadItems(Result.InProgress))
            val result = withContext(Dispatchers.IO) { getNotes.run(subject) }
            result.fold({
                setState(SubjectDetailsState.LoadItems(Result.Error(it)))
            }, {
                setState(SubjectDetailsState.LoadItems(Result.Success(it)))
            })
        }
    }
}
