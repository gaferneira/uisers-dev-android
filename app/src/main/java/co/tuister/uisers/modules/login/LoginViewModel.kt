package co.tuister.uisers.modules.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.base.Failure.AuthenticationError
import co.tuister.domain.entities.User
import co.tuister.domain.usecases.UserUseCase
import co.tuister.domain.usecases.login.LoginUseCase
import co.tuister.domain.usecases.login.LoginUseCase.Params
import co.tuister.domain.usecases.login.LogoutUseCase
import co.tuister.domain.usecases.login.SendVerifyLinkUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.modules.login.LoginState.ValidateLogin
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
  private val loginUseCase: LoginUseCase,
  private val userUseCase: UserUseCase,
  private val logoutUseCase: LogoutUseCase,
  private val sendVerifyLinkUseCase: SendVerifyLinkUseCase
) :
    BaseViewModel() {
    val email: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")

    fun doLogIn() {
        setState(ValidateLogin(Result.InProgress()))
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val emailText = email.value!!
                val result = loginUseCase.run(Params(emailText, password.value!!))
                result.fold({
                    // manage error
                    setState(ValidateLogin(Error(it)))
                }, {
                    if (it) {
                        doLoadUserData(emailText)
                    } else {
                        // password incorrect
                        setState(ValidateLogin(Error(AuthenticationError())))
                    }
                })
            }
        }
    }

    fun reSendConfirmEmail() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                sendVerifyLinkUseCase.run()
                logoutUseCase.run()
            }
        }
    }

    fun doLoadUserData(emailText: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val resultData = userUseCase.run()
                resultData.fold({ failure ->
                    setState(
                        ValidateLogin(
                            Result.Success(
                                User(
                                    emailText,
                                    emailText
                                )
                            )
                        )
                    )
                }, {
                    setState(ValidateLogin(Result.Success(it)))
                })
            }
        }
    }
}
