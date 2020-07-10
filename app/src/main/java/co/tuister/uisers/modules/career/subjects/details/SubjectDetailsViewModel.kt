package co.tuister.uisers.modules.career.subjects.details

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Note
import co.tuister.domain.entities.Subject
import co.tuister.domain.usecases.career.GetNotesUseCase
import co.tuister.domain.usecases.career.RemoveNoteUseCase
import co.tuister.domain.usecases.career.SaveNoteUseCase
import co.tuister.domain.usecases.career.SaveSubjectUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.InProgress
import co.tuister.uisers.utils.extensions.round
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectDetailsViewModel(
    private val getNotes: GetNotesUseCase,
    private val saveNote: SaveNoteUseCase,
    private val saveSubject: SaveSubjectUseCase,
    private val removeUseCase: RemoveNoteUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class LoadItems(result: Result<List<Note>>) : State<List<Note>>(result)
        class LoadAverage(result: Result<Float>) : State<Float>(result)
        class SaveNote(result: Result<Nothing>) : State<Nothing>(result)
        class RemoveItem(result: Result<Nothing>) : State<Nothing>(result)
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
            val result = withContext(Dispatchers.IO) { getNotes(subject) }
            result.fold(
                {
                    setState(State.LoadItems(Result.Error(it)))
                },
                {
                    setState(State.LoadItems(Result.Success(it)))
                    val average = calculateAverage(it)?.toFloat()
                    setState(State.LoadAverage(Result.Success(average)))
                    updateAverage(average?.round())
                }
            )
        }
    }

    private fun updateAverage(newNote: Float?) {
        viewModelScope.launch {
            val oldAverage = subject.note
            if (newNote != null && newNote != oldAverage) {
                subject.note = newNote
                saveSubject(subject)
            }
        }
    }

    private fun calculateAverage(data: List<Note>?): Double? {
        return data?.sumByDouble { it.total.toDouble() }
    }

    fun saveNote(note: Note) {
        viewModelScope.launch {
            saveNote(Pair(note, subject)).fold(
                {
                    setState(State.SaveNote(Result.Error(it)))
                },
                {
                    setState(State.SaveNote(Result.Success()))
                }
            )
        }
    }

    fun removeNote(item: Note) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { removeUseCase(item) }
            result.fold(
                {
                    setState(State.RemoveItem(Result.Error(it)))
                },
                {
                    setState(State.RemoveItem(Result.Success()))
                }
            )
        }
    }
}
