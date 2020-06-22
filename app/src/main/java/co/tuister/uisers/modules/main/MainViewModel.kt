package co.tuister.uisers.modules.main

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.User
import co.tuister.domain.usecases.FeedbackUseCase
import co.tuister.domain.usecases.MigrationUseCase
import co.tuister.domain.usecases.UserUseCase
import co.tuister.domain.usecases.login.DownloadImageUseCase
import co.tuister.domain.usecases.login.FCMUpdateUseCase
import co.tuister.domain.usecases.login.LogoutUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.InProgress
import co.tuister.uisers.utils.Result.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val downloadImageUseCase: DownloadImageUseCase,
    private val userUseCase: UserUseCase,
    private val fcmUpdateUseCase: FCMUpdateUseCase,
    private val migrationUseCase: MigrationUseCase,
    private val feedbackUserCase: FeedbackUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class ValidateLogout(val result: Result<Boolean>) : State<Boolean>(result)
        class DownloadedImage(val result: Result<Uri>) : State<Uri>(result)
    }

    val title: MutableLiveData<String> = MutableLiveData("Inicio")
    val name: MutableLiveData<String> = MutableLiveData("")
    val email: MutableLiveData<String> = MutableLiveData("")
    val career: MutableLiveData<String> = MutableLiveData("")
    val semester: MutableLiveData<String> = MutableLiveData("")
    val version: MutableLiveData<String> = MutableLiveData("")
    lateinit var user: User

    private var migration = false

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                fcmUpdateUseCase.run()
            }
        }

        viewModelScope.launch {
            if (!migration) {
                migration = true
                withContext(Dispatchers.IO) {
                    val success = migrationUseCase.run()
                    Timber.i("Result %s", success)
                }
            }
        }
    }

    fun setUserData(user: User?) {
        user?.let {
            email.value = it.email
            name.value = it.name
            career.value = it.career
            semester.value = it.semester
            this.user = it
        }
    }

    private fun downloadImage() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val resultData = downloadImageUseCase.run(DownloadImageUseCase.Params(user.email))
                resultData.fold(
                    {},
                    {
                        setState(State.DownloadedImage(Success(it)))
                    }
                )
            }
        }
    }

    fun downloadUserData() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val resultData = userUseCase.run()
                resultData.fold(
                    { _ ->
                        // logout
                        setState(State.ValidateLogout(Success(true)))
                    },
                    {
                        setUserData(it)
                        downloadImage()
                    }
                )
            }
        }
    }

    fun doLogOut() {
        setState(State.ValidateLogout(InProgress()))
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                logoutUseCase.run()
                setState(State.ValidateLogout(Success(true)))
            }
        }
    }

    fun sendFeedback(feedback: String) {
        viewModelScope.launch {
            feedbackUserCase.run(feedback)
        }
    }
}
