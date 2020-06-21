package co.tuister.uisers.common

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import co.tuister.domain.base.Failure
import co.tuister.uisers.R
import co.tuister.uisers.modules.login.register.RegisterFragment
import co.tuister.uisers.utils.extensions.showConfirmDialog
import co.tuister.uisers.utils.extensions.showDialog

open class BaseFragment : Fragment() {

    override fun onStart() {
        super.onStart()
        getTitle()?.let {
            activity?.title = it
        }
    }

    open fun getTitle(): CharSequence? = findNavController()?.currentDestination?.label

    fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    protected fun showDialog(message: Int, title: Int, unit: (() -> Unit)? = null) {
        parentFragmentManager.showDialog(requireContext(), message, title, unit)
    }

    protected fun showDialog(message: String, title: String, unit: (() -> Unit)? = null) {
        parentFragmentManager.showDialog(requireContext(), message, title, unit)
    }

    protected fun showConfirmDialog(
        message: Int,
        title: Int,
        negativeMessage: Int = android.R.string.cancel,
        unitNegative: (() -> Unit)? = null,
        unitPositive: (() -> Unit)? = null
    ) {
        parentFragmentManager.showConfirmDialog(
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
        parentFragmentManager.showConfirmDialog(
            requireContext(),
            message,
            title,
            negativeMessage,
            unitNegative,
            unitPositive
        )
    }

    protected fun launchImagePicker() {
        val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
        photoPickerIntent.type = "image/*"

        startActivityForResult(
            Intent.createChooser(photoPickerIntent, "Select Picture"),
            RegisterFragment.RESULT_LOAD_IMAGE
        )
    }

    protected fun manageFailure(failure: Failure?, showGenericMessage: Boolean = false): Boolean {
        when (failure) {
            is Failure.ServerError -> showConfirmDialog(R.string.alert_error_server, R.string.alert)
            is Failure.NetworkConnection -> showConfirmDialog(R.string.alert_check_internet, R.string.alert)
            else -> {
                if (showGenericMessage) {
                    showConfirmDialog(R.string.alert_error_try_again, R.string.alert)
                } else {
                    return false
                }
            }
        }

        return true
    }
}
