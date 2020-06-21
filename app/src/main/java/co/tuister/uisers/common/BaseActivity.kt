package co.tuister.uisers.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import co.tuister.domain.base.Failure
import co.tuister.uisers.R
import co.tuister.uisers.utils.ProgressType
import co.tuister.uisers.utils.extensions.showConfirmDialog
import co.tuister.uisers.utils.extensions.showDialog
import timber.log.Timber

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    protected fun showDialog(message: Int, title: Int, unit: (() -> Unit)? = null) {
        supportFragmentManager.showDialog(this, message, title, unit)
    }

    protected fun showDialog(message: String, title: String, unit: (() -> Unit)? = null) {
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
            is Failure.ServerError -> showConfirmDialog(R.string.alert_error_server, R.string.alert)
            is Failure.NetworkConnection -> showConfirmDialog(
                R.string.alert_check_internet,
                R.string.alert
            )
            else -> {

                failure?.error?.run {
                    Timber.e(this)
                }

                if (showGenericMessage) {
                    showConfirmDialog(R.string.alert_error_try_again, R.string.alert)
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
