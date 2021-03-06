package co.tuister.uisers.modules.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.User
import co.tuister.domain.usecases.UserUseCase
import co.tuister.domain.usecases.login.LoginUseCase
import co.tuister.domain.usecases.login.LoginUseCase.Params
import co.tuister.domain.usecases.login.LogoutUseCase
import co.tuister.domain.usecases.login.SendVerifyLinkUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val sendVerifyLinkUseCase: SendVerifyLinkUseCase,
    private val userUseCase: UserUseCase

) :
    BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class ValidateLogin(result: Result<User>) : State<User>(result)
    }

    val email: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")

    init {
        viewModelScope.launch {
            userUseCase().fold(
                {
                    if (it is Failure.EmailNotVerifiedError) {
                        setState(State.ValidateLogin(Error(it)))
                    }
                },
                {
                    setState(State.ValidateLogin(Result.Success()))
                }
            )
        }
    }

    fun doLogIn() {
        setState(State.ValidateLogin(Result.InProgress()))
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val emailText = email.value!!
                val result = loginUseCase(Params(emailText, password.value!!))
                result.fold(
                    {
                        // manage error
                        setState(State.ValidateLogin(Error(it)))
                    },
                    {
                        setState(State.ValidateLogin(Result.Success()))
                    }
                )
            }
        }
    }

    fun reSendConfirmEmail() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                sendVerifyLinkUseCase()
                logoutUseCase()
            }
        }
    }
}
