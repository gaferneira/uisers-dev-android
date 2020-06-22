package co.tuister.uisers.modules.career.subjects.add

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import co.tuister.domain.entities.Subject
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentSubjectAddBinding
import co.tuister.uisers.modules.career.subjects.add.AddSubjectViewModel.State
import co.tuister.uisers.utils.StringUtils
import co.tuister.uisers.utils.extensions.checkRequireFormFields
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
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
                    code = selectedItem.id
                }
            }
        }

        bindProgressButton(binding.buttonSave)
        binding.buttonSave.setOnClickListener {
            hideKeyboard()
            if (requireContext().checkRequireFormFields(
                binding.autocompleteSubject,
                binding.editTextCredits
            )
            ) {
                it.isEnabled = false
                if (subject.code.isEmpty()) {
                    subject.code = StringUtils.generateId()
                }
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
            is State.Save -> onSaveSubject(status)
            is State.LoadDefaultSubjects -> loadDefaultSubjects(status)
        }
    }

    private fun loadDefaultSubjects(state: State.LoadDefaultSubjects) {
        handleState(state) {
            val adapter = AutoCompleteSubjectsAdapter(requireContext(), it)
            binding.autocompleteSubject.setAdapter(adapter)
        }
    }

    private fun onSaveSubject(state: State.Save) {
        handleState(
            state,
            inProgress = {
                with(binding.buttonSave) {
                    showProgress {
                        buttonTextRes = R.string.progress_updating
                        progressColor = Color.WHITE
                        isEnabled = false
                    }
                }
            },
            onError = {
                binding.buttonSave.hideProgress(R.string.action_save)
                binding.buttonSave.isEnabled = true
                manageFailure(it)
            },
            onSuccess = {
                binding.buttonSave.hideProgress(R.string.action_save)
                binding.buttonSave.isEnabled = true
                findNavController().popBackStack()
            }
        )
    }

    companion object {
        const val SUBJECT_ARG = "SUBJECT_ARG"
    }
}
