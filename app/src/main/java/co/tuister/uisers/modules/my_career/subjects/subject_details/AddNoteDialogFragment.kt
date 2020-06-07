package co.tuister.uisers.modules.my_career.subjects.subject_details

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import co.tuister.domain.entities.Note
import co.tuister.domain.entities.Subject
import co.tuister.uisers.R
import co.tuister.uisers.databinding.DialogFragmentSubjectsAddNoteBinding
import co.tuister.uisers.utils.format

class AddNoteDialogFragment : AppCompatDialogFragment() {

    interface AddNoteDialogListener {
        fun onSaveNote(note: Note)
    }

    lateinit var binding: DialogFragmentSubjectsAddNoteBinding
    lateinit var note: Note
    lateinit var subject: Subject

    private var listener: AddNoteDialogListener? = null

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
            R.layout.dialog_fragment_subjects_add_note,
            null,
            false
        )
        binding.lifecycleOwner = this
        note = arguments?.getParcelable(ARGUMENT_NOTE) ?: Note()
        subject = arguments?.getParcelable(ARGUMENT_SUBJECT) ?: Subject()
        binding.subjectBinding = subject
        binding.noteBinding = note
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextPercentage.addTextChangedListener(
            EditorTextWatcher(binding.editTextPercentage, 100) {
                updateValues()
            }
        )

        binding.editTextGrade.addTextChangedListener(
            EditorTextWatcher(binding.editTextGrade, 5) {
                updateValues()
            })

        binding.buttonSave.isEnabled = note.title.isNotEmpty()
        binding.editTextDetail.addTextChangedListener {
            binding.buttonSave.isEnabled = it?.isNotEmpty() == true
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.buttonSave.setOnClickListener {
            listener?.onSaveNote(note)
            dismiss()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    private fun updateValues() {
        val total = (note.percentage * note.grade / 100.0).toFloat()

        var definitelyGrade: Float = subject.note - note.total
        definitelyGrade += total

        binding.editTextTotal.setText(total.format(2))
        binding.editTextFinalNote.setText(definitelyGrade.format(2))
    }

    internal class EditorTextWatcher(
      private var editText: EditText,
      private var max: Int,
      private val updateValues: (() -> Unit)?
    ) : TextWatcher {

        override fun afterTextChanged(s: Editable) {
            updateValues?.invoke()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                var value = s.toString().toFloat()
                if (value > max) {
                    while (value > max) {
                        value /= 10f
                    }
                    editText.setText(String.format("%1.1f", value).replace(",".toRegex(), "."))
                }
                editText.setSelection(editText.text.length)
            } catch (e: NumberFormatException) {
                // textViewValueTotal.setText("");
                // textViewValueDefinitelyGrade.setText("");
            }
        }
    }

    companion object {

        const val TAG = "AddNoteDialogFragment"
        private const val ARGUMENT_SUBJECT = "ARGUMENT_SUBJECT"
        private const val ARGUMENT_NOTE = "ARGUMENT_NOTE"

        fun create(
          subject: Subject,
          note: Note?,
          listener: AddNoteDialogListener
        ): AddNoteDialogFragment {
            val dialog = AddNoteDialogFragment()
            dialog.arguments = bundleOf(
                Pair(ARGUMENT_SUBJECT, subject),
                Pair(ARGUMENT_NOTE, note)
            )
            dialog.listener = listener
            return dialog
        }
    }
}
