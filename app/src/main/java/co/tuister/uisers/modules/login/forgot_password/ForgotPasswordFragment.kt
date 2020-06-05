package co.tuister.uisers.modules.login.forgot_password

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
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class ForgotPasswordFragment : BaseFragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
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
            hideKeyboard()
            viewModel.doRecover()
        }
    }

    private fun update(state: BaseState<Any>) {
        when (state) {
            is ForgotPasswordState.ValidateEmail -> validateEmail(state)
        }
    }

    private fun validateEmail(state: ForgotPasswordState.ValidateEmail) {
        when {
            state.inProgress() -> {
                binding.loginStatus.isVisible = true
            }
            state.isFailure() -> {
                binding.loginStatus.isVisible = false
                showDialog(R.string.error_result_forget_message, R.string.title_forget_title)
            }
            else -> {
                binding.loginStatus.isVisible = false
                showDialog(R.string.success_result_forget_message, R.string.title_forget_title) {
                    goBackToLogin()
                }
            }
        }
    }

    private fun goBackToLogin() {
        findNavController().popBackStack()
    }
}
