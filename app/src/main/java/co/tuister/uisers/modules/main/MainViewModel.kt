package co.tuister.uisers.modules.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.User
import co.tuister.domain.usecases.login.DownloadImageUseCase
import co.tuister.domain.usecases.login.LogoutUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.modules.main.MainState.DownloadedImage
import co.tuister.uisers.modules.main.MainState.ValidateLogout
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.InProgress
import co.tuister.uisers.utils.Result.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val downloadImageUseCase: DownloadImageUseCase
) : BaseViewModel() {
    val title: MutableLiveData<String> = MutableLiveData("Inicio")
    val name: MutableLiveData<String> = MutableLiveData("")
    val email: MutableLiveData<String> = MutableLiveData("")
    val career: MutableLiveData<String> = MutableLiveData("")
    val semester: MutableLiveData<String> = MutableLiveData("")
    val version: MutableLiveData<String> = MutableLiveData("")
    lateinit var user: User

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
                resultData.fold({ failure ->

                }, {
                    setState(DownloadedImage(Success(it)))
                })
            }
        }
    }

    fun doLogOut() {
        setState(ValidateLogout(InProgress()))
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                logoutUseCase.run()
                setState(ValidateLogout(Success(true)))
            }
        }
    }
}
