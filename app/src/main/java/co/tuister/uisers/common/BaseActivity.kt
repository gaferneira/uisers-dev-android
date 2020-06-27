package co.tuister.uisers.common

import androidx.appcompat.app.AppCompatActivity
import co.tuister.domain.base.Failure
import co.tuister.uisers.R
import co.tuister.uisers.utils.ProgressType
import co.tuister.uisers.utils.analytics.Analytics
import co.tuister.uisers.utils.extensions.showConfirmDialog
import co.tuister.uisers.utils.extensions.showDialog
import org.koin.android.ext.android.inject
import timber.log.Timber

open class BaseActivity : AppCompatActivity() {

    protected val analytics: Analytics by inject()

    protected fun showDialog(message: Int, title: Int, unit: (() -> Unit)? = null) {
        supportFragmentManager.showDialog(this, message, title, unit)
    }

    protected fun showConfirmDialog(
        message: Int,
        title: Int,
        negativeMessage: Int = android.R.string.cancel,
        unitNegative: (() -> Unit)? = null,
        unitPositive: (() -> Unit)? = null
    ) {
        supportFragmentManager.showConfirmDialog(
            this,
            message,
            title,
            negativeMessage,
            unitNegative,
            unitPositive
        )
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
                    manageFailure(it)
                }
            },
            onSuccess = {
                showDefaultProgress(false)
                onSuccess.invoke(it)
            }
        )
    }

    fun manageFailure(failure: Failure?, showGenericMessage: Boolean = false): Boolean {
        when (failure) {
            is Failure.ServerError -> showDialog(R.string.error_server, R.string.base_label_alert)
            is Failure.NetworkConnection -> showDialog(
                R.string.error_check_internet,
                R.string.base_label_alert
            )
            else -> {
                failure?.error?.run {
                    Timber.e(this)
                }

                if (showGenericMessage) {
                    showDialog(R.string.error_try_again, R.string.base_label_alert)
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
}
