package co.tuister.uisers.modules.main

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import co.tuister.uisers.R
import co.tuister.uisers.databinding.DialogFragmentFeedbackBinding
import co.tuister.uisers.utils.extensions.checkRequireFormFields

class FeedbackDialogFragment : AppCompatDialogFragment() {

    interface FeedbackDialogListener {
        fun onSendFeedback(feedback: String)
    }

    lateinit var binding: DialogFragmentFeedbackBinding

    private var listener: FeedbackDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppCompatDialog(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_fragment_feedback,
            null,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextFeedback.addTextChangedListener {
            binding.buttonSend.isEnabled =
                requireContext().checkRequireFormFields(binding.editTextFeedback, showError = false)
        }

        binding.buttonSend.setOnClickListener {
            listener?.onSendFeedback(binding.editTextFeedback.text.toString())
            dismiss()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    companion object {

        const val TAG = "FeedbackDialogFragment"

        fun create(
            listener: FeedbackDialogListener?
        ): FeedbackDialogFragment {
            val dialog = FeedbackDialogFragment()
            dialog.listener = listener
            return dialog
        }
    }
}
