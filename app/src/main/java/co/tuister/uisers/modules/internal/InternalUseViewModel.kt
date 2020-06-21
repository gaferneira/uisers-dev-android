package co.tuister.uisers.modules.internal

import android.content.Context
import androidx.lifecycle.viewModelScope
import co.tuister.domain.base.Failure.FormError
import co.tuister.domain.usecases.internal.DataUserUseCase
import co.tuister.domain.usecases.internal.UpdateDataCalendarUseCase
import co.tuister.domain.usecases.internal.UpdateDataCareersUseCase
import co.tuister.domain.usecases.internal.UpdateDataMapUseCase
import co.tuister.domain.usecases.internal.UpdateDataSubjectsUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.modules.internal.InternalUseViewModel.State.ValidateUserDocument
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class InternalUseViewModel(
    val dataUserUseCase: DataUserUseCase,
    val updateSubjects: UpdateDataSubjectsUseCase,
    val updateCareers: UpdateDataCareersUseCase,
    val updateMapData: UpdateDataMapUseCase,
    val updateCalendar: UpdateDataCalendarUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class ValidateUserDocument(val result: Result<Boolean>) : State<Boolean>(result)
        class UpdateSubjects(val result: Result<Boolean>) : State<Boolean>(result)
        class UpdateCareers(val result: Result<Boolean>) : State<Boolean>(result)
        class UpdateMapData(val result: Result<Boolean>) : State<Boolean>(result)
        class UpdateCalendarData(val result: Result<Boolean>) : State<Boolean>(result)
    }

    fun generateUserCSVData(context: Context) {
        setState(ValidateUserDocument(InProgress()))
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val result = dataUserUseCase.run()
                result.fold(
                    {
                        setState(ValidateUserDocument(Error(it)))
                    },
                    { list ->
                        try {
                            val HEADER = "carrera,correo,fcmId"
                            val SEPARATOR = ","
                            val file = File(context.getExternalFilesDir("uisers"), "data_users.csv")
                            if (file.exists())
                                file.delete()
                            file.createNewFile()
                            val fileWriter = file.bufferedWriter()
                            fileWriter.append(HEADER)
                            fileWriter.appendln()
                            list.forEach { user ->
                                user.run {
                                    fileWriter.append(career + SEPARATOR + email + SEPARATOR + fcmId)
                                }
                                fileWriter.appendln()
                            }
                            fileWriter.flush()
                            fileWriter.close()
                            setState(ValidateUserDocument(Success(true)))
                        } catch (e: Exception) {
                            setState(ValidateUserDocument(Error(FormError(e))))
                        }
                    }
                )
            }
        }
    }

    fun updateSubjects() {
        viewModelScope.launch {
            setState(State.UpdateSubjects(InProgress()))
            val result = updateSubjects.run()
            result.fold(
                {
                    setState(State.UpdateSubjects(Error(it)))
                },
                {
                    setState(State.UpdateSubjects(Success(it)))
                }
            )
        }
    }

    fun updateCareers() {
        viewModelScope.launch {
            setState(State.UpdateCareers(InProgress()))
            val result = updateCareers.run()
            result.fold(
                {
                    setState(State.UpdateCareers(Error(it)))
                },
                {
                    setState(State.UpdateCareers(Success(it)))
                }
            )
        }
    }

    fun updateMapData() {
        viewModelScope.launch {
            setState(State.UpdateMapData(InProgress()))
            val result = updateMapData.run()
            result.fold(
                {
                    setState(State.UpdateMapData(Error(it)))
                },
                {
                    setState(State.UpdateMapData(Success(it)))
                }
            )
        }
    }

    fun updateCalendarData() {
        viewModelScope.launch {
            setState(State.UpdateCalendarData(InProgress()))
            val result = updateCalendar.run()
            result.fold(
                {
                    setState(State.UpdateCalendarData(Error(it)))
                },
                {
                    setState(State.UpdateCalendarData(Success(it)))
                }
            )
        }
    }
}
