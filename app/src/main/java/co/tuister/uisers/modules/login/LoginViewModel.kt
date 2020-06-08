package co.tuister.uisers.modules.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.User
import co.tuister.domain.usecases.UserUseCase
import co.tuister.domain.usecases.login.LoginUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val loginUseCase: LoginUseCase, private val userUseCase: UserUseCase) :
    BaseViewModel() {
    val email: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")

    fun doLogIn() {
        setState(LoginState.ValidateLogin(Result.InProgress()))
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val emailText = email.value!!
                val result = loginUseCase.run(LoginUseCase.Params(emailText, password.value!!))
                result.fold({
                    // manage error
                    setState(LoginState.ValidateLogin(Result.Error(it)))
                }, {
                    if (it) {
                        doLoadUserData(emailText)
                    } else {
                        // password incorrect
                        setState(LoginState.ValidateLogin(Result.Error(Failure.AuthenticationError())))
                    }
                })
            }
        }
    }

    fun doLoadUserData(emailText: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val resultData = userUseCase.run()
                resultData.fold({ failure ->
                    setState(
                        LoginState.ValidateLogin(
                            Result.Success(
                                User(
                                    emailText,
                                    emailText
                                )
                            )
                        )
                    )
                }, {
                    setState(LoginState.ValidateLogin(Result.Success(it)))
                })
            }
        }
    }
}
