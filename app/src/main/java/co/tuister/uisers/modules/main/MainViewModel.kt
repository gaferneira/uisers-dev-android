package co.tuister.uisers.modules.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.User
import co.tuister.domain.usecases.login.LogoutUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val logoutUseCase: LogoutUseCase) : BaseViewModel() {
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

    fun doLogOut() {
        setState(MainState.ValidateLogout(Result.InProgress))
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                logoutUseCase.run()
                setState(MainState.ValidateLogout(Result.Success(true)))
            }
        }
    }
}
