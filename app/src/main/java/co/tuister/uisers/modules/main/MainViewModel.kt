package co.tuister.uisers.modules.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.User
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

class MainViewModel(
  private val logoutUseCase: LogoutUseCase,
  private val downloadImageUseCase: DownloadImageUseCase,
  private val userUseCase: UserUseCase,
  private val fcmUpdateUseCase: FCMUpdateUseCase,
  private val migrationUseCase: MigrationUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class ValidateLogout(val result: Result<Boolean>) : State<Boolean>(result)
        class DownloadedImage(val result: Result<Uri>) : State<Uri>(result)
    }

    sealed class Event {
        object GoToLogin : Event()
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
                val success = migrationUseCase.run()
                Log.i("migration", "Result " + success)
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

    fun downloadImage() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val resultData = downloadImageUseCase.run(DownloadImageUseCase.Params(user.email))
                resultData.fold({}, {
                    setState(State.DownloadedImage(Success(it)))
                })
            }
        }
    }

    fun downloadUserData() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val resultData = userUseCase.run()
                resultData.fold({ failure ->
                }, {
                    setUserData(it)
                })
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
}
