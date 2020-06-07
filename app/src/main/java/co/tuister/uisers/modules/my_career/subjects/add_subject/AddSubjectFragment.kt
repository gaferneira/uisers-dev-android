package co.tuister.uisers.modules.my_career.subjects.add_subject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import co.tuister.domain.entities.Subject
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentSubjectAddBinding
import co.tuister.uisers.utils.checkRequireFormFields
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class AddSubjectFragment : BaseFragment() {

    private lateinit var binding: FragmentSubjectAddBinding
    private lateinit var viewModel: AddSubjectViewModel

    private lateinit var subject: Subject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadArguments()
    }

    private fun loadArguments() {
        subject = arguments?.getParcelable(SUBJECT_ARG) ?: Subject()
    }

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubjectAddBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.subjectBinding = subject
        initViews()
        initViewModel()
        return binding.root
    }

    private fun initViews() {

        with(binding.autocompleteSubject) {
            isEnabled = subject.name.isEmpty()
            setOnItemClickListener { adapterView, _, position, _ ->
                val selectedItem =
                    (adapterView.adapter as AutoCompleteSubjectsAdapter).getItem(position)
                binding.subjectBinding = subject.apply {
                    credits = selectedItem.credits
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            hideKeyboard()
            if (requireContext().checkRequireFormFields(binding.autocompleteSubject, binding.editTextCredits)) {
                it.isEnabled = false
                viewModel.saveSubject(subject)
            }
        }
    }

    private fun initViewModel() {
        viewModel = getViewModel()
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                update(it)
            }
        }
        viewModel.initialize()
    }

    private fun update(status: BaseState<Any>?) {
        when (status) {
            is AddSubjectsState.Save -> onSaveSubject(status)
            is AddSubjectsState.LoadDefaultSubjects -> loadDefaultSubjects(status)
        }
    }

    private fun loadDefaultSubjects(status: AddSubjectsState.LoadDefaultSubjects) {
        if (status.isSuccess()) {
            val adapter = AutoCompleteSubjectsAdapter(requireContext(), status.data)
            binding.autocompleteSubject.setAdapter(adapter)
        }
    }

    private fun onSaveSubject(state: AddSubjectsState.Save) {
        when {
            state.inProgress() -> {
                // show loading }
            }
            state.isFailure() -> {
                // show error
                binding.buttonSave.isEnabled = true
            }
            else -> {
                findNavController().popBackStack()
            }
        }
    }

    companion object {
        const val SUBJECT_ARG = "SUBJECT_ARG"
    }
}
