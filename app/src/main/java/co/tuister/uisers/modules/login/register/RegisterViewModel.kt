package co.tuister.uisers.modules.login.register

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.base.Failure.FormError
import co.tuister.domain.entities.Career
import co.tuister.domain.entities.User
import co.tuister.domain.usecases.login.*
import co.tuister.domain.usecases.login.RegisterUseCase.Params
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.ProgressType.DOWNLOADING
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(
  val registerUseCase: RegisterUseCase,
  val careersUseCase: CareersUseCase,
  val logoutUseCase: LogoutUseCase,
  val campusUseCase: CampusUseCase,
  val uploadImageUseCase: UploadImageUseCase
) :
    BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class ValidateRegister(val result: Result<User>) : State<User>(result)
    }

    val userLive = MutableLiveData(User())
    val password1 = MutableLiveData<String>()
    val password2 = MutableLiveData<String>()
    val listCareers = mutableListOf<Career>()
    val listCampus = mutableListOf<String>()
    private var uri: Uri? = null

    fun doRegister() {
        setState(State.ValidateRegister(InProgress()))
        val pass1 = password1.value
        val pass2 = password2.value
        val user = userLive.value
        user?.let {
            var errorMessage = ""
            when {
                it.email.isEmpty() -> {
                    errorMessage = "Email can not be empty"
                }
                it.name.isEmpty() -> {
                    errorMessage = "Name can not be empty"
                }
                pass1.isNullOrEmpty() || pass2.isNullOrEmpty() -> {
                    errorMessage = "Password fields can not be empty"
                }
                pass1 != pass2 -> {
                    errorMessage = "Passwords do not match"
                }
                it.career.isEmpty() -> {
                    errorMessage = "You need to pick a career"
                }
                it.semester.isEmpty() -> {
                    errorMessage = "You need to pick an entry year"
                }
            }

            if (errorMessage.isEmpty()) {
                registerUser()
            } else {
                setState(State.ValidateRegister(Error(FormError(Exception(errorMessage)))))
            }
        }
    }

    private fun registerUser() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val result = registerUseCase.run(Params(userLive.value!!, password1.value!!))
                result.fold({ fail ->
                    setState(State.ValidateRegister(Error(fail)))
                }, { res ->
                    if (res) {
                        runUploadRoutine()
                    }
                })
            }
        }
    }

    fun runUploadRoutine() {
        uri?.let {
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    uploadImageUseCase.run(
                        UploadImageUseCase.Params(
                            it,
                            userLive.value!!.email
                        )
                    )
                }
            }
        }
        setState(State.ValidateRegister(Success()))
    }

    fun setImageUri(uri: Uri) {
        this.uri = uri
    }

    fun doLogout(unit: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                logoutUseCase.run()
                unit.invoke()
            }
        }
    }

    fun pickCareer(pos: Int) {
        userLive.value?.apply {
            listCareers[pos].let {
                career = it.name
                idCareer = it.codigo
            }
        }
    }

    fun pickCampus(pos: Int) {
        userLive.value?.apply {
            campus = listCampus[pos]
        }
    }

    fun pickYear(year: String) {
        userLive.value?.apply {
            semester = year
        }
    }

    fun getCareers(unit: () -> Unit) {
        if (listCareers.isEmpty()) {
            setState(State.ValidateRegister(InProgress(DOWNLOADING)))
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    val result =
                        careersUseCase.run()
                    result.fold({ fail ->
                        setState(State.ValidateRegister(Error(fail)))
                    }, { res ->
                        listCareers.addAll(res)
                        unit.invoke()
                    })
                }
            }
        } else {
            unit.invoke()
        }
    }

    fun getCampus(unit: () -> Unit) {
        if (listCampus.isEmpty()) {
            setState(State.ValidateRegister(InProgress(DOWNLOADING)))
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    val result =
                        campusUseCase.run()
                    result.fold({ fail ->
                        setState(State.ValidateRegister(Error(fail)))
                    }, { res ->
                        listCampus.addAll(res)
                        unit.invoke()
                    })
                }
            }
        } else {
            unit.invoke()
        }
    }
}
