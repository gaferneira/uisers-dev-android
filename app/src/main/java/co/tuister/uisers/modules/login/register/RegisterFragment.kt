package co.tuister.uisers.modules.login.register

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import co.tuister.domain.base.Failure
import co.tuister.domain.base.Failure.FormError
import co.tuister.domain.entities.Career
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseActivity
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentLoginRegisterBinding
import co.tuister.uisers.modules.login.LoginActivity
import co.tuister.uisers.modules.login.register.RegisterViewModel.State.ValidateRegister
import co.tuister.uisers.utils.ProgressType.DOWNLOADING
import co.tuister.uisers.utils.extensions.checkRequireFormFields
import co.tuister.uisers.utils.extensions.launchImagePicker
import co.tuister.uisers.utils.extensions.singleClick
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel
import java.util.Calendar.YEAR
import java.util.Calendar.getInstance

class RegisterFragment : BaseFragment<FragmentLoginRegisterBinding>() {

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginRegisterBinding.inflate(inflater)
        binding.lifecycleOwner = this
        initViewModel()
        binding.registerViewModel = viewModel
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.btnRegister.singleClick {
            if (requireContext().checkRequireFormFields(
                binding.etRegisterEmail,
                binding.etRegisterName,
                binding.etRegisterPassword,
                binding.etRegisterConfirmPassword
            )
            ) {
                hideKeyboard()
                viewModel.doRegister()
            }
        }

        binding.editTextCareer.singleClick {
            viewModel.getCareers {
                binding.loginStatus.isVisible = false
                showCareerOptions()
            }
        }

        binding.editTextCampus.singleClick {
            viewModel.getCampus {
                binding.loginStatus.isVisible = false
                showCampusOptions()
            }
        }

        binding.editTextYear.singleClick {
            showYearOptions()
        }

        binding.circleImagePhoto.singleClick {
            (requireActivity() as BaseActivity).launchImagePicker()
        }
    }

    private fun showCareerOptions() {
        val options = viewModel.listCareers.map { careerOption(it) }.toTypedArray()
        // setup the alert builder
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Pick a career")
            .setItems(options) { _, which ->
                binding.editTextCareer.setText(options[which])
                viewModel.pickCareer(which)
            }
            .create()

        dialog.show()
    }

    private fun showCampusOptions() {
        val options = viewModel.listCampus.toTypedArray()
        // setup the alert builder
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Pick a campus")
            .setItems(options) { _, which ->
                binding.editTextCampus.setText(options[which])
                viewModel.pickCampus(which)
            }
            .create()

        dialog.show()
    }

    private fun showYearOptions() {
        val currentYear = getInstance().get(YEAR) + 1
        val valuesList = List(MAX_NUM_ROWS) {
            currentYear - (it)
        }
        val options = valuesList.map { yearOption(it) }.toTypedArray()
        // setup the alert builder
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Pick your entry year")
            .setItems(options) { _, which ->
                binding.editTextYear.setText(options[which])
                viewModel.pickYear(options[which])
            }
            .create()

        dialog.show()
    }

    private fun yearOption(value: Int): String {
        return value.toString()
    }

    private fun careerOption(value: Career): String {
        return value.codigo + " - " + value.name
    }

    private fun initViewModel() {
        viewModel = getViewModel()
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                update(it)
            }
        }
        binding.registerViewModel = viewModel
    }

    private fun update(state: BaseState<Any>) {
        when (state) {
            is ValidateRegister -> validateRegister(state)
        }
    }

    private fun validateRegister(state: ValidateRegister) {
        handleState(
            state,
            inProgress = {
                if (it == DOWNLOADING) {
                    binding.loginStatusMessage.text =
                        context?.getString(R.string.base_progress_downloading)
                } else {
                    binding.loginStatusMessage.text =
                        context?.getString(R.string.login_progress_signing_in)
                }
                binding.loginStatus.isVisible = true
            },
            onError = {
                onRegisterError(it)
            },
            onSuccess = {
                binding.loginStatus.isVisible = false
                showDialog(
                    getString(R.string.login_message_login_register_confirm_email, viewModel.userLive.value?.email),
                    getString(R.string.login_title_dialog_login_register)
                ) {
                    viewModel.doLogout {
                        goToLogin()
                    }
                }
                analytics.trackUserSignUp()
            }
        )
    }

    private fun onRegisterError(it: Failure?) {
        binding.loginStatus.isVisible = false
        when (it) {
            is FormError -> {
                showDialog(
                    it.error!!.message!!,
                    requireContext().getString(R.string.login_title_dialog_login_register)
                )
            }
            is Failure.AuthWeakPasswordException -> {
                showDialog(
                    "Password is to weak to create a account",
                    requireContext().getString(R.string.login_title_dialog_login_register)
                )
            }
            else -> {
                if (!manageFailure(it)) {
                    showDialog(
                        it?.error?.localizedMessage
                            ?: "Lo sentimos no pudimos crear una cuenta con ese correo. Intenta colocando uno nuevo.",
                        requireContext().getString(R.string.login_title_dialog_login_register)
                    )
                }
            }
        }
    }

    private fun goToLogin() {
        activity?.let {
            LoginActivity.start(requireContext())
            it.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RESULT_LOAD_IMAGE -> {
                    val uri = data?.data
                    CropImage.activity(uri)
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(true)
                        .start(requireContext(), this)
                }
                CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    binding.circleImagePhoto.setImageURI(result.uri)
                    viewModel.setImageUri(result.uri)
                }
            }
        }
    }

    companion object {
        const val RESULT_LOAD_IMAGE = 11
        const val MAX_NUM_ROWS = 20
    }
}
