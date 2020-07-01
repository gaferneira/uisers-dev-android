package co.tuister.uisers.common

import androidx.appcompat.app.AppCompatActivity
import co.tuister.domain.base.Failure
import co.tuister.uisers.R
import co.tuister.uisers.utils.ProgressType
import co.tuister.uisers.utils.analytics.Analytics
import co.tuister.uisers.utils.extensions.showDialog
import org.koin.android.ext.android.inject
import timber.log.Timber

open class BaseActivity : AppCompatActivity() {

    protected val analytics: Analytics by inject()

    protected fun showDialog(message: Int, title: Int, unit: (() -> Unit)? = null) {
        supportFragmentManager.showDialog(this, message, title, unit)
    }

    protected fun <T : Any> handleState(
        state: BaseState<T>,
        inProgress: ((ProgressType) -> Unit)? = null,
        onError: ((Failure?) -> Unit)? = null,
        onSuccess: (T?) -> Unit
    ) {
        state.handleResult(
            inProgress = {
                inProgress?.invoke(it) ?: showDefaultProgress(true)
            },
            onError = {
                onError?.invoke(it) ?: run {
                    showDefaultProgress(false)
                    manageFailure(it) { text, _ ->
                        showBanner(text)
                    }
                }
            },
            onSuccess = {
                showDefaultProgress(false)
                onSuccess.invoke(it)
            }
        )
    }

    fun manageFailure(failure: Failure?, showGenericMessage: Boolean = false, displayMessage: (text: Int, priority: Int) -> Unit): Boolean {
        when (failure) {
            is Failure.ServerError ->
                displayMessage(R.string.error_server, 1)
            is Failure.NetworkConnection ->
                displayMessage(R.string.error_check_internet, 1)
            else -> {
                failure?.error?.run {
                    Timber.e(this)
                }
                if (showGenericMessage) {
                    displayMessage(R.string.error_try_again, 1)
                } else {
                    return false
                }
            }
        }
        return true
    }

    open fun showDefaultProgress(show: Boolean) {
        // Optional
    }

    open fun showBanner(text: Int, textButton: Int = android.R.string.ok) {
        // Replace with material banner
        showDialog(text, R.string.base_label_alert)
    }
}
