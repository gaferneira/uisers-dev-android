package co.tuister.uisers.modules.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import co.tuister.domain.entities.Career
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseActivity
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentProfileBinding
import co.tuister.uisers.modules.internal.InternalActivity
import co.tuister.uisers.modules.login.register.RegisterFragment
import co.tuister.uisers.modules.profile.ProfileViewModel.State.DownloadedImage
import co.tuister.uisers.modules.profile.ProfileViewModel.State.LoadData
import co.tuister.uisers.modules.profile.ProfileViewModel.State.ValidateProfileUpdate
import co.tuister.uisers.utils.extensions.setImageFromUri
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private val safeArgs by navArgs<ProfileFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    private fun initViews() {

        bindProgressButton(binding.buttonSave)

        binding.buttonUploadPicture.setOnClickListener {
            launchImagePicker()
        }
        binding.buttonInternal.setOnClickListener {
            InternalActivity.start(requireContext())
        }
        binding.editTextCareer.setOnClickListener {
            viewModel.getCareers {
                showCareerOptions()
            }
        }

        binding.editTextCampus.setOnClickListener {
            viewModel.getCampus {
                showCampusOptions()
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
        viewModel.initialize(safeArgs.extraUser)
        binding.viewModel = viewModel
        binding.activity = activity as BaseActivity
    }

    private fun showCareerOptions() {
        val options = viewModel.listCareers.map { careerOption(it) }.toTypedArray()
        // setup the alert builder
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Pick a career")
            .setItems(options) { _, which ->
                binding.editTextCareer.setText(options[which])
            }
            .create()

        dialog.show()
    }

    private fun careerOption(value: Career): String {
        return value.codigo + " - " + value.name
    }

    private fun showCampusOptions() {
        val options = viewModel.listCampus.toTypedArray()
        // setup the alert builder
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Pick a campus")
            .setItems(options) { _, which ->
                binding.editTextCampus.setText(options[which])
            }
            .create()

        dialog.show()
    }

    private fun update(status: BaseState<Any>?) {
        when (status) {
            is DownloadedImage -> downloadImage(status)
            is ValidateProfileUpdate -> processProfile(status)
            is LoadData -> loadData(status)
        }
    }

    private fun loadData(state: LoadData) {
        handleState(
            state,
            inProgress = {
                binding.loadingStatusMessage.text =
                    context?.getString(R.string.progress_downloading_data)
                binding.loadingStatus.isVisible = true
            },
            onError = {
                binding.loadingStatus.isVisible = false
            },
            onSuccess = {
                binding.loadingStatus.isVisible = false
            }
        )
    }

    private fun processProfile(state: ValidateProfileUpdate) {
        handleState(
            state,
            inProgress = {
                with(binding.buttonSave) {
                    showProgress {
                        buttonTextRes = R.string.profile_progress_updating
                        progressColor = Color.WHITE
                        isEnabled = false
                    }
                }
            },
            onError = {
                manageFailure(it)
                binding.buttonSave.hideProgress(R.string.action_save)
                binding.buttonSave.isEnabled = true
            },
            onSuccess = {
                binding.buttonSave.hideProgress(R.string.action_save)
                binding.buttonSave.isEnabled = true
            }
        )
    }

    private fun downloadImage(state: DownloadedImage) {
        handleState(state) {
            binding.circleImagePhoto.setImageFromUri(state.result.data)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RegisterFragment.RESULT_LOAD_IMAGE -> {
                    val uri = data?.data
                    CropImage.activity(uri)
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(true)
                        .start(requireContext(), this)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    viewModel.uploadImage(result.uri)
                    binding.circleImagePhoto.setImageURI(result.uri)
                }
            }
        }
    }
}
