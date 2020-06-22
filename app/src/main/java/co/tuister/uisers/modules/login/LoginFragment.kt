package co.tuister.uisers.modules.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import co.tuister.domain.base.Failure.EmailNotVerifiedError
import co.tuister.uisers.BuildConfig
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentLoginBinding
import co.tuister.uisers.modules.internal.InternalActivity
import co.tuister.uisers.modules.login.LoginViewModel.State
import co.tuister.uisers.modules.main.MainActivity
import co.tuister.uisers.utils.analytics.Analytics
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class LoginFragment : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
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
        binding.loginViewModel = viewModel
    }

    private fun initViews() {

        binding.buttonInternal.isVisible = BuildConfig.DEBUG
        binding.buttonInternal.setOnClickListener {
            InternalActivity.start(requireContext())
        }

        binding.loginSignInButton.setOnClickListener {
            hideKeyboard()
            viewModel.doLogIn()
            analytics.trackEvent(Analytics.EVENT_CLICK_LOGIN)
        }

        binding.loginRegisterButton.setOnClickListener {
            hideKeyboard()
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentDestToRegisterFragmentDest())
        }

        binding.forgotPass.setOnClickListener {
            hideKeyboard()
            findNavController().navigate(R.id.action_login_fragment_dest_to_forgot_password_fragment_dest)
        }
    }

    private fun update(state: BaseState<Any>) {
        when (state) {
            is State.ValidateLogin -> validateLogin(state)
        }
    }

    private fun validateLogin(state: State.ValidateLogin) {
        handleState(
            state,
            inProgress = {
                binding.loginStatus.isVisible = true
            },
            onError = {
                binding.loginStatus.isVisible = false
                when (it) {
                    is EmailNotVerifiedError -> {
                        showConfirmDialog(
                            R.string.error_result_login_message_not_verified_email,
                            R.string.title_login,
                            R.string.action_resend,
                            {
                                viewModel.reSendConfirmEmail()
                            }
                        )
                    }
                    else -> {
                        showDialog(R.string.error_result_login_message, R.string.title_login)
                    }
                }
            },
            onSuccess = {
                binding.loginStatus.isVisible = false
                analytics.trackUserLogin()
                goToMain()
            }
        )
    }

    private fun goToMain() {
        activity?.let {
            MainActivity.start(requireContext())
            it.finish()
        }
    }
}
