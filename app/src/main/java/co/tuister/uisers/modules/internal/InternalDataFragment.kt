package co.tuister.uisers.modules.internal

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class InternalDataFragment : BaseFragment<FragmentInternalDataBinding>() {

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
        handleButtonState(state, binding.buttonDownloadUsesCsv)
    }

    private fun updateSubjects(state: UpdateSubjects) {
        handleButtonState(state, binding.buttonUpdateSubjects)
    }

    private fun updateCareers(state: UpdateCareers) {
        handleButtonState(state, binding.buttonUpdateCareers)
    }

    private fun updateMapData(state: UpdateMapData) {
        handleButtonState(state, binding.buttonUpdateMapData)
    }

    private fun updateCalendarData(state: UpdateCalendarData) {
        handleButtonState(state, binding.buttonCalendarData)
    }

    private fun <T : Any> handleButtonState(
        state: BaseState<T>,
        button: Button
    ) {
        handleState(
            state,
            inProgress = {
                with(button) {
                    if (button.tag !is String) {
                        button.tag = text.toString()
                    }
                    showProgress {
                        buttonTextRes = if (it == DOWNLOADING)
                            R.string.progress_downloading_data
                        else
                            R.string.progress_updating
                        progressColor = Color.DKGRAY
                    }
                    isEnabled = false
                }
            },
            onError = {
                button.hideProgress(button.tag.toString())
                button.isEnabled = true
                manageFailure(it, true)
            },
            onSuccess = {
                button.hideProgress(button.tag.toString())
                button.isEnabled = true
            }
        )
    }
}
