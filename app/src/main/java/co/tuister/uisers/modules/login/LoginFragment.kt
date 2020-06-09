package co.tuister.uisers.modules.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import co.tuister.domain.base.Failure.EmailNotVerifiedError
import co.tuister.domain.entities.User
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentLoginBinding
import co.tuister.uisers.modules.main.MainActivity
import co.tuister.uisers.utils.Result
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
        initListeners()
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

    private fun initListeners() {
        binding.loginSignInButton.setOnClickListener {
            hideKeyboard()
            viewModel.doLogIn()
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
            is LoginState.ValidateLogin -> validateLogin(state)
        }
    }

    private fun validateLogin(state: LoginState.ValidateLogin) {
        when (val result = state.result){
             is Result.InProgress-> {
                binding.loginStatus.isVisible = true
            }
            is Result.Error -> {
                binding.loginStatus.isVisible = false
                when (result.exception) {
                    is EmailNotVerifiedError -> {
                        showDialog(
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
            }
            is Result.Success -> {
                binding.loginStatus.isVisible = false
                goToMain(result.data)
            }
        }
    }

    private fun goToMain(user: User?) {
        activity?.let {
            MainActivity.start(requireContext(), user)
            it.finish()
        }
    }
}
