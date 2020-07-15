package co.tuister.uisers.modules.career.semesters

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import co.tuister.domain.entities.Semester
import co.tuister.uisers.databinding.DialogFragmentCareerSemesterBinding
import co.tuister.uisers.utils.extensions.checkRequireFormFields
import co.tuister.uisers.utils.extensions.singleClick
import java.util.*

class AddSemesterDialogFragment : AppCompatDialogFragment() {

    interface AddSemesterDialogListener {
        fun onSaveSemester(semester: Semester)
    }

    lateinit var binding: DialogFragmentCareerSemesterBinding
    private lateinit var semesters: List<Semester>

    var semester: Semester? = null

    private var listener: AddSemesterDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppCompatDialog(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentCareerSemesterBinding.inflate(LayoutInflater.from(context))
        semesters =
            arguments?.getParcelableArray(ARGUMENT_SEMESTERS)?.map { it as Semester } ?: listOf()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextSemester.singleClick {
            showListDialog()
        }
    }

    private fun showListDialog() {

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val semestersAvailable =
            List(NUM_SEMESTERS) {
                Pair(
                    (currentYear - (it / 2)).toString(),
                    (2 - (it % 2)).toString()
                )
            }.filter { pair ->
                semesters.firstOrNull { it.period == pair.first + "-" + pair.second } == null
            }

        val options = semestersAvailable.map { it.first + "-" + it.second }.toTypedArray()

        // setup the alert builder
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select semester")
            .setItems(options) { _, which ->
                val selected = semestersAvailable[which]
                semester = Semester("", selected.first + "-" + selected.second)
                binding.editTextSemester.setText(options[which])
            }
            .create()

        dialog.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.buttonSave.singleClick {
            val validateFields = requireContext().checkRequireFormFields(binding.editTextSemester)
            if (validateFields) {
                semester?.run {
                    current = binding.switchCurrent.isChecked
                    listener?.onSaveSemester(this)
                    dismiss()
                }
            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    companion object {
        const val TAG = "AddSemesterDialogFragment"
        private const val ARGUMENT_SEMESTERS = "ARGUMENT_PERIOD"
        private const val NUM_SEMESTERS = 20

        fun create(
            semesters: List<Semester>?,
            listener: AddSemesterDialogListener?
        ): AddSemesterDialogFragment {
            val dialog = AddSemesterDialogFragment()
            dialog.arguments = bundleOf(
                Pair(ARGUMENT_SEMESTERS, semesters?.toTypedArray())
            )
            dialog.listener = listener
            return dialog
        }
    }
}
