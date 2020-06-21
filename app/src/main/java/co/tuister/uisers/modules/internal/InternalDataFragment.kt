package co.tuister.uisers.modules.internal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseActivity
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentInternalDataBinding
import co.tuister.uisers.modules.internal.InternalUseViewModel.State.UpdateCalendarData
import co.tuister.uisers.modules.internal.InternalUseViewModel.State.UpdateCareers
import co.tuister.uisers.modules.internal.InternalUseViewModel.State.UpdateMapData
import co.tuister.uisers.modules.internal.InternalUseViewModel.State.UpdateSubjects
import co.tuister.uisers.modules.internal.InternalUseViewModel.State.ValidateUserDocument
import co.tuister.uisers.utils.ProgressType.DOWNLOADING
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.InProgress
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class InternalDataFragment : BaseFragment() {

    private lateinit var binding: FragmentInternalDataBinding
    private lateinit var viewModel: InternalUseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInternalDataBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    private fun initViews() {
        binding.buttonDownloadUsesCsv.setOnClickListener {
            viewModel.generateUserCSVData(requireContext())
        }
        binding.buttonUpdateSubjects.setOnClickListener {
            viewModel.updateSubjects()
        }
        binding.buttonUpdateCareers.setOnClickListener {
            viewModel.updateCareers()
        }
        binding.buttonUpdateMapData.setOnClickListener {
            viewModel.updateMapData()
        }
        binding.buttonCalendarData.setOnClickListener {
            viewModel.updateCalendarData()
        }
        binding.buttonMaterial.setOnClickListener {
            findNavController().navigate(R.id.action_internal_to_material)
        }
    }

    private fun initViewModel() {
        viewModel = getViewModel()
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                update(it)
            }
        }
        binding.viewModel = viewModel
        binding.activity = activity as BaseActivity
    }

    private fun update(status: BaseState<Any>?) {
        when (status) {
            is ValidateUserDocument -> processDocument(status)
            is UpdateSubjects -> updateSubjects(status)
            is UpdateCareers -> updateCareers(status)
            is UpdateMapData -> updateMapData(status)
            is UpdateCalendarData -> updateCalendarData(status)
        }
    }

    private fun processDocument(state: ValidateUserDocument) {
        when {
            state.isSuccess() -> {
                binding.loadingStatus.isVisible = false
            }
            state.inProgress() -> {
                val st = state.result as InProgress
                if (st.type == DOWNLOADING) {
                    binding.loadingStatusMessage.text =
                        context?.getString(R.string.progress_downloading_data)
                } else {
                    binding.loadingStatusMessage.text =
                        context?.getString(R.string.profile_progress_updating)
                }
                binding.loadingStatus.isVisible = true
            }
            state.isFailure() -> {
                binding.loadingStatus.isVisible = false
            }
        }
    }

    private fun updateSubjects(status: UpdateSubjects) {
        when (val result = status.result) {
            is Result.InProgress -> {
                binding.loadingStatus.isVisible = true
                binding.buttonUpdateSubjects.isEnabled = false
                binding.loadingStatusMessage.text =
                    context?.getString(R.string.progress_updating)
            }
            is Result.Error -> {
                binding.loadingStatus.isVisible = false
                binding.buttonUpdateSubjects.isEnabled = true
                manageFailure(result.exception, true)
            }
            is Result.Success -> {
                binding.loadingStatus.isVisible = false
                binding.buttonUpdateSubjects.isEnabled = true
            }
        }
    }

    private fun updateCareers(status: UpdateCareers) {
        when (val result = status.result) {
            is InProgress -> {
                binding.loadingStatus.isVisible = true
                binding.buttonUpdateCareers.isEnabled = false
                context?.getString(R.string.progress_updating)
            }
            is Result.Error -> {
                binding.loadingStatus.isVisible = false
                binding.buttonUpdateCareers.isEnabled = true
                manageFailure(result.exception, true)
            }
            is Result.Success -> {
                binding.loadingStatus.isVisible = false
                binding.buttonUpdateCareers.isEnabled = true
            }
        }
    }

    private fun updateMapData(status: UpdateMapData) {
        when (val result = status.result) {
            is InProgress -> {
                binding.loadingStatus.isVisible = true
                binding.buttonUpdateMapData.isEnabled = false
                context?.getString(R.string.progress_updating)
            }
            is Result.Error -> {
                binding.loadingStatus.isVisible = false
                binding.buttonUpdateMapData.isEnabled = true
                manageFailure(result.exception, true)
            }
            is Result.Success -> {
                binding.loadingStatus.isVisible = false
                binding.buttonUpdateMapData.isEnabled = true
            }
        }
    }

    private fun updateCalendarData(status: UpdateCalendarData) {
        when (val result = status.result) {
            is InProgress -> {
                binding.loadingStatus.isVisible = true
                binding.buttonCalendarData.isEnabled = false
                context?.getString(R.string.progress_updating)
            }
            is Result.Error -> {
                binding.loadingStatus.isVisible = false
                binding.buttonCalendarData.isEnabled = true
                manageFailure(result.exception, true)
            }
            is Result.Success -> {
                binding.loadingStatus.isVisible = false
                binding.buttonCalendarData.isEnabled = true
            }
        }
    }
}
