package co.tuister.uisers.modules.internal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseActivity
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentInternalDataBinding
import co.tuister.uisers.modules.internal.InternalUseViewModel.State.ValidateUserDocument
import co.tuister.uisers.utils.ProgressType.DOWNLOADING
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
}
