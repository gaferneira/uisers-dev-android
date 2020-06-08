package co.tuister.uisers.modules.my_career.subjects.subject_details

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Note
import co.tuister.domain.entities.Subject
import co.tuister.domain.usecases.my_career.GetNotesUseCase
import co.tuister.domain.usecases.my_career.SaveNoteUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.InProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectDetailsViewModel(
  private val getNotes: GetNotesUseCase,
  private val saveNote: SaveNoteUseCase
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
            setState(SubjectDetailsState.LoadItems(InProgress()))
            val result = withContext(Dispatchers.IO) { getNotes.run(subject) }
            result.fold({
                setState(SubjectDetailsState.LoadItems(Result.Error(it)))
            }, {
                setState(SubjectDetailsState.LoadItems(Result.Success(it)))
            })
        }
    }

    fun saveNote(note: Note) {
        viewModelScope.launch {
            saveNote.run(Pair(note, subject)).fold({
                setState(SubjectDetailsState.SaveNote(Result.Error(it)))
            }, {
                setState(SubjectDetailsState.SaveNote(Result.Success()))
            })
        }
    }
}
