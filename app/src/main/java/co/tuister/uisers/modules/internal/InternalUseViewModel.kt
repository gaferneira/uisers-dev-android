package co.tuister.uisers.modules.internal

import android.content.Context
import androidx.lifecycle.viewModelScope
import co.tuister.domain.base.Failure.FormError
import co.tuister.domain.usecases.DataUserUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.modules.internal.InternalUseViewModel.State.ValidateUserDocument
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class InternalUseViewModel(val dataUserUseCase: DataUserUseCase) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class ValidateUserDocument(val result: Result<Boolean>) : State<Boolean>(result)
    }

    fun generateUserCSVData(context: Context) {
        setState(ValidateUserDocument(InProgress()))
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val result = dataUserUseCase.run()
                result.fold({
                    setState(ValidateUserDocument(Error(it)))
                }, { list ->
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
                })
            }
        }
    }
}