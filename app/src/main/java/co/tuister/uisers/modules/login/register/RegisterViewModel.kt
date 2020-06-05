package co.tuister.uisers.modules.login.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.base.Failure.FormError
import co.tuister.domain.entities.User
import co.tuister.domain.usecases.login.RegisterUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.modules.login.register.RegisterState.ValidateRegister
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.Error
import co.tuister.uisers.utils.Result.Success
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(val registerUseCase: RegisterUseCase) : BaseViewModel() {
    val userLive = MutableLiveData(User())
    val password1 = MutableLiveData<String>()
    val password2 = MutableLiveData<String>()

    fun doRegister() {
        setState(ValidateRegister(Result.InProgress))
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
            }
            if (errorMessage.isEmpty()) {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        val result =
                            registerUseCase.run(RegisterUseCase.Params(userLive.value!!, pass1!!))
                        result.fold({ fail ->
                            if (fail.error is FirebaseAuthWeakPasswordException) {
                                setState(ValidateRegister(Error(FormError(Exception("Password is to weak to create a account")))))
                            } else {
                                setState(ValidateRegister(Error(fail)))
                            }
                        }, { res ->
                            if (res) {
                                setState(ValidateRegister(Success()))
                            }
                        })
                    }
                }
            } else {
                setState(ValidateRegister(Error(FormError(Exception(errorMessage)))))
            }
        }
    }
}
