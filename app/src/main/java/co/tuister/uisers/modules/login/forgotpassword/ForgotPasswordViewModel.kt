package co.tuister.uisers.modules.login.forgotpassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.base.Failure
import co.tuister.domain.usecases.login.RecoverPasswordUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPasswordViewModel(
    private val recoverPassword: RecoverPasswordUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class ValidateEmail(val result: Result<Nothing>) : State<Nothing>(result)
    }

    val email: MutableLiveData<String> = MutableLiveData("")

    fun doRecover() {
        setState(State.ValidateEmail(Result.InProgress()))
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val sentRecover = recoverPassword.run(email.value!!)
                sentRecover.fold(
                    {
                        setState(State.ValidateEmail(Result.Error(it)))
                    },
                    { success ->
                        if (success) {
                            setState(State.ValidateEmail(Result.Success()))
                        } else {
                            setState(State.ValidateEmail(Result.Error(Failure.AuthenticationError())))
                        }
                    }
                )
            }
        }
    }
}
