package co.tuister.uisers.modules.login.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import co.tuister.domain.base.Failure.FormError
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentRegisterBinding
import co.tuister.uisers.modules.login.LoginActivity
import co.tuister.uisers.modules.login.register.RegisterState.ValidateRegister
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class RegisterFragment : BaseFragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        binding.lifecycleOwner = this
        initViewModel()
        binding.registerViewModel = viewModel
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.btnRegister.setOnClickListener {
            hideKeyboard()
            viewModel.doRegister()
        }
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
        when {
            state.inProgress() -> {
                binding.loginStatus.isVisible = true
            }
            state.isFailure() -> {
                binding.loginStatus.isVisible = false
                when (val st = (state.result as Result.Error).exception) {
                    is FormError -> {
                        showDialog(
                            st.error!!.message!!,
                            requireContext().getString(R.string.title_dialog_view_register)
                        )
                    }
                    else -> {
                        showDialog(
                            "Lo sentimos no pudimos crear una cuenta con ese correo. Intenta colocando uno nuevo.",
                            requireContext().getString(R.string.title_dialog_view_register)
                        )
                    }
                }
            }
            state.isSuccess() -> {
                binding.loginStatus.isVisible = false
                showDialog(
                    "Te hemos enviado un correo para que confirmes tu correo y puedas acceder a la aplicación a" + viewModel.userLive.value?.email,
                    requireContext().getString(R.string.title_dialog_view_register)
                ) {
                    goToLogin()
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
}
