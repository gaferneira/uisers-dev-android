package co.tuister.uisers.modules.career.subjects.details

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
import co.tuister.uisers.databinding.DialogFragmentCareerSubjectsNoteBinding
import co.tuister.uisers.utils.extensions.format
import co.tuister.uisers.utils.extensions.singleClick
import java.text.DecimalFormat

class AddNoteDialogFragment : AppCompatDialogFragment() {

    interface AddNoteDialogListener {
        fun onSaveNote(note: Note)
    }

    lateinit var binding: DialogFragmentCareerSubjectsNoteBinding
    lateinit var note: Note
    lateinit var subject: Subject

    private var maxPercentage: Float = HUNDRED_PERCENT

    private var listener: AddNoteDialogListener? = null

    private var originalSubjectNote: Float = 0f

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
            R.layout.dialog_fragment_career_subjects_note,
            null,
            false
        )
        binding.lifecycleOwner = this
        note = arguments?.getParcelable(ARGUMENT_NOTE) ?: Note()
        subject = arguments?.getParcelable(ARGUMENT_SUBJECT) ?: Subject()
        maxPercentage = arguments?.getFloat(ARGUMENT_MAX_PERCENTAGE) ?: HUNDRED_PERCENT
        binding.subjectBinding = subject
        binding.noteBinding = note
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        originalSubjectNote = subject.note - note.total

        if (maxPercentage == 0f) {
            binding.editTextPercentage.isEnabled = false
        }

        binding.editTextPercentage.addTextChangedListener(
            EditorTextWatcher(binding.editTextPercentage, maxPercentage) {
                updateValues()
            }
        )

        binding.editTextGrade.addTextChangedListener(
            EditorTextWatcher(binding.editTextGrade, MAX_NOTE) {
                updateValues()
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.buttonSave.singleClick {
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

        val total = (note.percentage * note.grade / HUNDRED_PERCENT)
        val definitelyGrade = originalSubjectNote + total

        binding.editTextTotal.setText(total.format(2))
        binding.editTextFinalNote.setText(definitelyGrade.format(2))

        note.total = total
    }

    internal class EditorTextWatcher(
        private var editText: EditText,
        private var max: Float,
        private val updateValues: (() -> Unit)?
    ) : TextWatcher {

        override fun afterTextChanged(s: Editable) {
            updateValues?.invoke()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // No op
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                var value = s.toString().toFloat()
                if (value > max) {
                    while (value > max) {
                        value /= DECIMAL
                    }

                    val text = DecimalFormat("##.#").format(value)
                    editText.setText(text.replace(",".toRegex(), "."))
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
        private const val ARGUMENT_MAX_PERCENTAGE = "ARGUMENT_PERCENTAGE"
        private const val HUNDRED_PERCENT: Float = 100f
        private const val DECIMAL: Float = 10f
        private const val MAX_NOTE: Float = 5f

        fun create(
            subject: Subject,
            note: Note?,
            maxPercentage: Float,
            listener: AddNoteDialogListener
        ): AddNoteDialogFragment {
            val dialog = AddNoteDialogFragment()
            dialog.arguments = bundleOf(
                Pair(ARGUMENT_SUBJECT, subject),
                Pair(ARGUMENT_NOTE, note),
                Pair(ARGUMENT_MAX_PERCENTAGE, maxPercentage)
            )
            dialog.listener = listener
            return dialog
        }
    }
}
