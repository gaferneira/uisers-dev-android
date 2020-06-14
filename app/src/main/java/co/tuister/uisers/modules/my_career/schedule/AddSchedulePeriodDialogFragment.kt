package co.tuister.uisers.modules.my_career.schedule

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.entities.Subject
import co.tuister.uisers.R
import co.tuister.uisers.databinding.DialogFragmentSchedulePeriodBinding
import co.tuister.uisers.utils.checkRequireFormFields
import co.tuister.uisers.utils.pickTime

class AddSchedulePeriodDialogFragment : AppCompatDialogFragment() {

    interface AddSchedulePeriodDialogListener {
        fun onSavePeriod(period: SchedulePeriod)
    }

    lateinit var binding: DialogFragmentSchedulePeriodBinding
    lateinit var subjects: List<Subject>
    lateinit var period: SchedulePeriod
    lateinit var requireTextViews: Array<TextView>

    private var listener: AddSchedulePeriodDialogListener? = null

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
            R.layout.dialog_fragment_schedule_period,
            null,
            false
        )
        binding.lifecycleOwner = this
        subjects = arguments?.getParcelableArray(ARGUMENT_SUBJECTS)?.map { it as Subject } ?: listOf()
        period = arguments?.getParcelable(ARGUMENT_PERIOD) ?: SchedulePeriod()
        requireTextViews = arrayOf(binding.autocompleteSubject,
            binding.editTextPlace,
            binding.editTextInitial,
            binding.editTextFinal)
        binding.periodBinding = period
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateButtonState()
        requireTextViews.forEach {
            it.addTextChangedListener {
                updateButtonState()
            }
        }

        with(binding.autocompleteSubject) {
            setAdapter(AutoCompleteSubjectsAdapter(requireContext(), subjects))
            setOnItemClickListener { adapterView, _, position, _ ->
                val selectedItem =
                    (adapterView.adapter as AutoCompleteSubjectsAdapter).getItem(position)
                binding.periodBinding = period.apply {
                    description = selectedItem.name
                }
            }
        }

        binding.editTextInitial.setOnClickListener {
            showTimeDialog(binding.editTextInitial)
        }

        binding.editTextFinal.setOnClickListener {
            showTimeDialog(binding.editTextFinal)
        }
    }

    private fun showTimeDialog(editText: EditText) {

        var hours = 0
        var minutes = 0

        val arrayTime = editText.text.toString().split(":".toRegex())
        if (arrayTime.size == 2) {
            hours = arrayTime[0].toInt()
            minutes = arrayTime[1].toInt()
        }

        requireContext().pickTime(hours, minutes) { selectedHour, selectedMinute ->
            editText.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.buttonSave.setOnClickListener {
            listener?.onSavePeriod(period)
            dismiss()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    private fun updateButtonState() {
        binding.buttonSave.isEnabled =
            requireContext().checkRequireFormFields(*requireTextViews, showError = false)
    }

    companion object {
        private const val ARGUMENT_SUBJECTS = "ARGUMENT_SUBJECTS"
        private const val ARGUMENT_PERIOD = "ARGUMENT_PERIOD"

        fun create(
          schedulePeriod: SchedulePeriod?,
          subjects: List<Subject>?,
          listener: AddSchedulePeriodDialogListener?
        ): AddSchedulePeriodDialogFragment {
            val dialog = AddSchedulePeriodDialogFragment()
            dialog.arguments = bundleOf(
                Pair(ARGUMENT_PERIOD, schedulePeriod),
                Pair(ARGUMENT_SUBJECTS, subjects?.toTypedArray())
            )
            dialog.listener = listener
            return dialog
        }
    }
}
