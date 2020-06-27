package co.tuister.uisers.modules.login.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentForgotPasswordBinding
import co.tuister.uisers.modules.login.forgotpassword.ForgotPasswordViewModel.State
import co.tuister.uisers.utils.analytics.Analytics
import co.tuister.uisers.utils.extensions.checkRequireFormFields
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>() {

    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater)
        binding.lifecycleOwner = this
        initViewModel()
        initViews()
        return binding.root
    }

    private fun initViewModel() {
        viewModel = getViewModel()
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                update(it)
            }
        }
        binding.forgotPasswordViewModel = viewModel
    }

    private fun initViews() {
        binding.btnRecover.setOnClickListener {
            if (requireContext().checkRequireFormFields(binding.tvLoginEmail)) {
                hideKeyboard()
                analytics.trackEvent(Analytics.EVENT_FORGOT_PASSWORD)
                viewModel.doRecover()
            }
        }
    }

    private fun update(state: BaseState<Any>) {
        when (state) {
            is State.ValidateEmail -> validateEmail(state)
        }
    }

    private fun validateEmail(state: State.ValidateEmail) {
        handleState(
            state,
            inProgress = {
                binding.loginStatus.isVisible = true
            },
            onError = {
                binding.loginStatus.isVisible = false
                if (!manageFailure(it)) {
                    showDialog(
                        it?.error?.localizedMessage ?: getString(R.string.error_result_login_message),
                        getString(R.string.login_title_login_forget_title)
                    )
                }
            },
            onSuccess = {
                binding.loginStatus.isVisible = false
                showDialog(R.string.login_message_result_forget_message, R.string.login_title_login_forget_title) {
                    goBackToLogin()
                }
            }
        )
    }

    private fun goBackToLogin() {
        findNavController().popBackStack()
    }
}
