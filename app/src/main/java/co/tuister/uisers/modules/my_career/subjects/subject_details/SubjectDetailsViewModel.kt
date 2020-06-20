package co.tuister.uisers.modules.my_career.subjects.subject_details

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Note
import co.tuister.domain.entities.Subject
import co.tuister.domain.usecases.my_career.GetNotesUseCase
import co.tuister.domain.usecases.my_career.SaveNoteUseCase
import co.tuister.domain.usecases.my_career.SaveSubjectUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.InProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectDetailsViewModel(
    private val getNotes: GetNotesUseCase,
    private val saveNote: SaveNoteUseCase,
    private val saveSubject: SaveSubjectUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class LoadItems(result: Result<List<Note>>) : State<List<Note>>(result)
        class LoadAverage(result: Result<Float>) : State<Float>(result)
        class SaveNote(result: Result<Nothing>) : State<Nothing>(result)
    }

    lateinit var subject: Subject

    fun initialize(subject: Subject) {
        this.subject = subject
    }

    fun refresh() {
        updateNotes()
    }

    private fun updateNotes() {
        viewModelScope.launch {
            setState(State.LoadItems(InProgress()))
            val result = withContext(Dispatchers.IO) { getNotes.run(subject) }
            result.fold(
                {
                    setState(State.LoadItems(Result.Error(it)))
                },
                {
                    setState(State.LoadItems(Result.Success(it)))
                    val average = calculateAverage(it)?.toFloat()
                    setState(State.LoadAverage(Result.Success(average)))
                    updateAverage(average)
                }
            )
        }
    }

    private fun updateAverage(newNote: Float?) {
        viewModelScope.launch {
            val oldAverage = subject.note
            if (newNote != null && newNote != oldAverage) {
                subject.note = newNote
                saveSubject.run(subject)
            }
        }
    }

    private fun calculateAverage(data: List<Note>?): Double? {
        return data?.sumByDouble { it.total.toDouble() }
    }

    fun saveNote(note: Note) {
        viewModelScope.launch {
            saveNote.run(Pair(note, subject)).fold(
                {
                    setState(State.SaveNote(Result.Error(it)))
                },
                {
                    setState(State.SaveNote(Result.Success()))
                }
            )
        }
    }
}
