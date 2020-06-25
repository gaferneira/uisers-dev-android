package co.tuister.uisers.common

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import co.tuister.domain.base.Failure
import co.tuister.uisers.utils.ProgressType
import co.tuister.uisers.utils.analytics.Analytics
import co.tuister.uisers.utils.extensions.showConfirmDialog
import co.tuister.uisers.utils.extensions.showDialog
import org.koin.android.ext.android.inject

open class BaseFragment<T : ViewBinding> : Fragment() {

    private var _binding: T? = null
    protected var binding
        get() = _binding!!
        set(value) {
            _binding = value
        }

    protected val analytics: Analytics by inject()

    override fun onStart() {
        super.onStart()
        getTitle()?.let {
            activity?.title = it
        }
    }

    open fun getTitle(): CharSequence? = findNavController().currentDestination?.label

    override fun onResume() {
        super.onResume()
        activity?.let {
            analytics.trackScreenView(it, this::class.java.simpleName, this::class.java.simpleName)
        }
    }

    fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    protected fun showDialog(message: Int, title: Int, unit: (() -> Unit)? = null) {
        childFragmentManager.showDialog(requireContext(), message, title, unit)
    }

    protected fun showDialog(message: String, title: String, unit: (() -> Unit)? = null) {
        childFragmentManager.showDialog(requireContext(), message, title, unit)
    }

    protected fun showConfirmDialog(
        message: Int,
        title: Int,
        negativeMessage: Int = android.R.string.cancel,
        unitNegative: (() -> Unit)? = null,
        unitPositive: (() -> Unit)? = null
    ) {
        childFragmentManager.showConfirmDialog(
            requireContext(),
            message,
            title,
            negativeMessage,
            unitNegative,
            unitPositive
        )
    }

    protected fun showConfirmDialog(
        message: String,
        title: String,
        negativeMessage: Int = android.R.string.cancel,
        unitNegative: (() -> Unit)? = null,
        unitPositive: (() -> Unit)? = null
    ) {
        childFragmentManager.showConfirmDialog(
            requireContext(),
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
        return (activity as? BaseActivity)?.manageFailure(failure, showGenericMessage) ?: false
    }

    open fun showDefaultProgress(show: Boolean) {
        // Optional
    }
}
