package co.tuister.uisers.modules.login.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import co.tuister.domain.entities.User
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.databinding.FragmentSplashBinding
import co.tuister.uisers.modules.login.LoginEvent.GoToLogin
import co.tuister.uisers.modules.login.LoginEvent.GoToMain
import co.tuister.uisers.modules.main.MainActivity
import org.koin.android.viewmodel.ext.android.getViewModel

class SplashFragment : BaseFragment() {

    private lateinit var binding: FragmentSplashBinding
    private lateinit var splashViewModel: SplashViewModel

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater)
        binding.lifecycleOwner = this
        splashViewModel = getViewModel()
        binding.splashViewModel = splashViewModel
        initListeners()
        splashViewModel.runInitialChecks()
        return binding.root
    }

    private fun initListeners() {
        splashViewModel.events.observe(viewLifecycleOwner, Observer {
            when (it) {
                is GoToLogin -> findNavController().navigate(R.id.action_splash_fragment_dest_to_login_fragment_dest)
                is GoToMain -> goToMain(it.user)
            }
        })
    }

    private fun goToMain(user: User) {
        activity?.let {
            MainActivity.start(requireContext(), user)
            it.finish()
        }
    }
}
