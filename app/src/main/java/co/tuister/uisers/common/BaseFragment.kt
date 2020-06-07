package co.tuister.uisers.common

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import co.tuister.uisers.R
import co.tuister.uisers.utils.UisersDialogFragment

open class BaseFragment : Fragment() {
    fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    protected fun showDialog(message: Int, title: Int, unit: (() -> Unit)? = null) {
        UisersDialogFragment.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.ok, View.OnClickListener {
                unit?.invoke()
            })
            .create().show(parentFragmentManager, UisersDialogFragment.UISERS_DIALOG_TAG)
    }

    protected fun showDialog(message: String, title: String, unit: (() -> Unit)? = null) {
        UisersDialogFragment.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.ok, View.OnClickListener {
                unit?.invoke()
            })
            .create().show(parentFragmentManager, UisersDialogFragment.UISERS_DIALOG_TAG)
    }
}
