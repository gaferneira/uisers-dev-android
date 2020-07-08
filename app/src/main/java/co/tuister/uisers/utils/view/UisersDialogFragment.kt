package co.tuister.uisers.utils.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import co.tuister.uisers.R
import co.tuister.uisers.databinding.DialogFragmentUisersBinding
import co.tuister.uisers.utils.extensions.singleClick
import co.tuister.uisers.utils.view.UisersDialogFragment.Builder.Companion.ARGUMENT_CANCELABLE
import co.tuister.uisers.utils.view.UisersDialogFragment.Builder.Companion.ARGUMENT_MESSAGE
import co.tuister.uisers.utils.view.UisersDialogFragment.Builder.Companion.ARGUMENT_MESSAGE_PARAMS
import co.tuister.uisers.utils.view.UisersDialogFragment.Builder.Companion.ARGUMENT_NEGATIVE_BUTTON
import co.tuister.uisers.utils.view.UisersDialogFragment.Builder.Companion.ARGUMENT_POSITIVE_BUTTON
import co.tuister.uisers.utils.view.UisersDialogFragment.Builder.Companion.ARGUMENT_TITLE
import co.tuister.uisers.utils.view.UisersDialogFragment.Builder.Companion.ARGUMENT_TITLE_PARAMS

class UisersDialogFragment : AppCompatDialogFragment() {
    class Builder(private val context: Context) {

        companion object {
            const val ARGUMENT_TITLE = "ARGUMENT_TITLE"
            const val ARGUMENT_TITLE_PARAMS = "ARGUMENT_TITLE_PARAMS"
            const val ARGUMENT_MESSAGE = "ARGUMENT_MESSAGE"
            const val ARGUMENT_MESSAGE_PARAMS = "ARGUMENT_MESSAGE_PARAMS"
            const val ARGUMENT_POSITIVE_BUTTON = "ARGUMENT_POSITIVE_BUTTON"
            const val ARGUMENT_NEGATIVE_BUTTON = "ARGUMENT_NEGATIVE_BUTTON"
            const val ARGUMENT_CANCELABLE = "ARGUMENT_CANCELABLE"
        }

        private val arguments = Bundle()
        private var onPositiveButtonClickListener: OnClickListener? = null
        private var onNegativeButtonClickListener: OnClickListener? = null

        init {
            setCancelable(true)
        }

        fun setTitle(@StringRes titleId: Int, vararg params: String): Builder {
            arguments.putString(ARGUMENT_TITLE, context.getString(titleId))
            arguments.putStringArray(ARGUMENT_TITLE_PARAMS, params)
            return this
        }

        fun setTitle(title: String, vararg params: String): Builder {
            arguments.putString(ARGUMENT_TITLE, title)
            arguments.putStringArray(ARGUMENT_TITLE_PARAMS, params)
            return this
        }

        fun setMessage(@StringRes messageId: Int, vararg params: String): Builder {
            arguments.putString(ARGUMENT_MESSAGE, context.getString(messageId))
            arguments.putStringArray(ARGUMENT_MESSAGE_PARAMS, params)
            return this
        }

        fun setMessage(message: String, vararg params: String): Builder {
            arguments.putString(ARGUMENT_MESSAGE, message)
            arguments.putStringArray(ARGUMENT_MESSAGE_PARAMS, params)
            return this
        }

        fun setPositiveButton(@StringRes textId: Int, listener: OnClickListener? = null): Builder {
            arguments.putString(ARGUMENT_POSITIVE_BUTTON, context.getString(textId))
            onPositiveButtonClickListener = listener
            return this
        }

        fun setNegativeButton(@StringRes textId: Int, listener: OnClickListener? = null): Builder {
            arguments.putString(ARGUMENT_NEGATIVE_BUTTON, context.getString(textId))
            onNegativeButtonClickListener = listener
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            arguments.putBoolean(ARGUMENT_CANCELABLE, cancelable)
            return this
        }

        fun create(): UisersDialogFragment {
            val dialog = UisersDialogFragment()
            dialog.arguments = arguments
            dialog.onPositiveButtonClickListener = onPositiveButtonClickListener
            dialog.onNegativeButtonClickListener = onNegativeButtonClickListener
            return dialog
        }
    }

    companion object {
        const val UISERS_DIALOG_TAG = "UISERS_DIALOG_TA"
    }

    lateinit var binding: DialogFragmentUisersBinding
    private var onPositiveButtonClickListener: OnClickListener? = null
    private var onNegativeButtonClickListener: OnClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppCompatDialog(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(
                context
            ),
            R.layout.dialog_fragment_uisers,
            null,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.apply {
            setLabelText(binding.textViewTitle, ARGUMENT_TITLE, ARGUMENT_TITLE_PARAMS)
            setLabelText(binding.textViewMessage, ARGUMENT_MESSAGE, ARGUMENT_MESSAGE_PARAMS)
            setButtonText(binding.buttonPositive, ARGUMENT_POSITIVE_BUTTON)
            setButtonText(binding.buttonNegative, ARGUMENT_NEGATIVE_BUTTON)
            isCancelable = getBoolean(ARGUMENT_CANCELABLE)
        }

        binding.buttonPositive.singleClick {
            dismiss()
            onPositiveButtonClickListener?.onClick(it)
        }

        binding.buttonNegative.singleClick {
            dismiss()
            onNegativeButtonClickListener?.onClick(it)
        }
    }

    private fun setLabelText(textView: TextView, key: String, keyParams: String) {
        val text = arguments?.getString(key)
        val params = arguments?.getStringArray(keyParams) as Array<*>
        if (text?.isNotEmpty() == true) {
            textView.text = text.format(params)
        } else {
            textView.visibility = GONE
        }
    }

    private fun setButtonText(button: Button, key: String) {
        val text = arguments?.getString(key)
        if (text?.isNotEmpty() == true) {
            button.text = text
        } else {
            button.visibility = GONE
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }
}
