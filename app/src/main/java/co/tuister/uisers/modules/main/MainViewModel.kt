package co.tuister.uisers.modules.main

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.User
import co.tuister.domain.usecases.FeedbackUseCase
import co.tuister.domain.usecases.UserUseCase
import co.tuister.domain.usecases.login.DownloadImageUseCase
import co.tuister.domain.usecases.login.FCMUpdateUseCase
import co.tuister.domain.usecases.login.LogoutUseCase
import co.tuister.domain.usecases.login.UploadImageUseCase
import co.tuister.domain.usecases.profile.DisableFirstTimeUseCase
import co.tuister.domain.usecases.profile.FirstTimeUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.modules.main.MainViewModel.State.FirsTime
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.InProgress
import co.tuister.uisers.utils.Result.Success
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val downloadImageUseCase: DownloadImageUseCase,
    private val userUseCase: UserUseCase,
    private val fcmUpdateUseCase: FCMUpdateUseCase,
    private val feedbackUserCase: FeedbackUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val firstTimeUseCase: FirstTimeUseCase,
    private val disableFirstTimeUseCase: DisableFirstTimeUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class ValidateLogout(val result: Result<Boolean>) : State<Boolean>(result)
        class DownloadedImage(val result: Result<Uri>) : State<Uri>(result)
        class FirsTime(val result: Result<Boolean>) : State<Boolean>(result)
    }

    val title: MutableLiveData<String> = MutableLiveData("Inicio")
    val name: MutableLiveData<String> = MutableLiveData("")
    val email: MutableLiveData<String> = MutableLiveData("")
    val career: MutableLiveData<String> = MutableLiveData("")
    val semester: MutableLiveData<String> = MutableLiveData("")
    val version: MutableLiveData<String> = MutableLiveData("")
    lateinit var user: User

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                fcmUpdateUseCase()
            }
        }
    }

    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                uploadImageUseCase(UploadImageUseCase.Params(uri, email.value!!))
            }
        }
    }

    private fun setUserData(user: User?) {
        user?.let {
            email.value = it.email
            name.value = it.name
            career.value = it.career
            semester.value = it.semester
            this.user = it
            FirebaseCrashlytics.getInstance().setUserId(user.fcmId)
        }
    }

    private fun downloadImage() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val resultData = downloadImageUseCase(DownloadImageUseCase.Params(user.email))
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
                val resultData = userUseCase()
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

    fun disableFirstTime() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                disableFirstTimeUseCase()
            }
        }
    }

    fun checkAnimationFirstTime() = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            val resultData = firstTimeUseCase()
            resultData.fold(
                {
                    setState(FirsTime(Success(true)))
                },
                {
                    setState(FirsTime(Success(it)))
                }
            )
        }
    }

    fun doLogOut() {
        setState(State.ValidateLogout(InProgress()))
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                logoutUseCase()
                setState(State.ValidateLogout(Success(true)))
            }
        }
    }

    fun sendFeedback(feedback: String) {
        viewModelScope.launch {
            feedbackUserCase(feedback)
        }
    }
}
