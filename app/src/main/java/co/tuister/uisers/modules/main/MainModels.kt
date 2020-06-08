package co.tuister.uisers.modules.main

import android.net.Uri
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class MainState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class ValidateLogout(val result: Result<Boolean>) : MainState<Boolean>(result)
    class DownloadedImage(val result: Result<Uri>) : MainState<Uri>(result)
}

sealed class MainEvent {
    object GoToLogin : MainEvent()
}
