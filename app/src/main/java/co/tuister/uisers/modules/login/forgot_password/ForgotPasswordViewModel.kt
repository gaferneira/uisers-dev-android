package co.tuister.uisers.modules.login.forgot_password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.base.Failure
import co.tuister.domain.usecases.login.RecoverPasswordUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPasswordViewModel(
    private val recoverPassword: RecoverPasswordUseCase
) : BaseViewModel() {

    val email: MutableLiveData<String> = MutableLiveData("")

    fun doRecover() {
        setState(ForgotPasswordState.ValidateEmail(Result.InProgress))
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val sentRecover = recoverPassword.run(email.value!!)
                sentRecover.fold({
                    setState(ForgotPasswordState.ValidateEmail(Result.Error(it)))
                }, { success ->
                    if (success) {
                        setState(ForgotPasswordState.ValidateEmail(Result.Success()))
                    } else {
                        setState(ForgotPasswordState.ValidateEmail(Result.Error(Failure.AuthenticationError())))
                    }
                })
            }
        }
    }
}
