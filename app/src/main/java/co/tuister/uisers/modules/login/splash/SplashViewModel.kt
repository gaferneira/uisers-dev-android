package co.tuister.uisers.modules.login.splash

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.User
import co.tuister.domain.usecases.UserUseCase
import co.tuister.uisers.R
import co.tuister.uisers.common.SingleLiveEvent
import co.tuister.uisers.utils.view.ThemeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class SplashViewModel(
    private val userUseCase: UserUseCase,
    private val themeProvider: ThemeProvider,
    private val context: Context
) : ViewModel() {

    sealed class Event {
        class GoToMain(val user: User) : Event()
        object GoToLogin : Event()
    }

    val message: MutableLiveData<String> = MutableLiveData("")
    val events: SingleLiveEvent<Event> = SingleLiveEvent()

    fun runInitialChecks() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                message.value = context.getString(R.string.login_message_splash_progress_checking_session)

                lateinit var userResult: Either<Failure, User>
                val checkTime = measureTimeMillis {
                    userResult = userUseCase()
                }

                // Theme
                val theme = themeProvider.getThemeFromPreferences()
                AppCompatDelegate.setDefaultNightMode(theme)

                val delayTime = if (checkTime > MIN_SPLASH_TIME) 0
                else MIN_SPLASH_TIME - checkTime

                when (val it = userResult) {
                    is Either.Left -> {
                        message.value = context.getString(R.string.login_message_splash_progress_go_to_login)
                        delay(delayTime)
                        events.value = Event.GoToLogin
                    }
                    is Either.Right -> {
                        message.value = context.getString(R.string.login_message_splash_progress_check_notes)
                        delay(delayTime)
                        events.value = Event.GoToMain(it.value)
                    }
                }
            }
        }
    }

    companion object {
        const val MIN_SPLASH_TIME = 2000
    }
}
