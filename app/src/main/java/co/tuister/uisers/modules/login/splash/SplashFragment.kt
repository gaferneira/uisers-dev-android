package co.tuister.uisers.modules.login.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.databinding.FragmentSplashBinding
import co.tuister.uisers.modules.login.LoginActivity.Companion.EXTRA_DEEP_LINK
import co.tuister.uisers.modules.login.splash.SplashViewModel.Event.GoToLogin
import co.tuister.uisers.modules.login.splash.SplashViewModel.Event.GoToMain
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
        splashViewModel.events.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is GoToLogin ->
                        findNavController()
                            .navigate(R.id.action_splash_fragment_dest_to_login_fragment_dest)
                    is GoToMain -> goToMain()
                }
            }
        )
    }

    private fun goToMain() {
        activity?.let {
            if (it.intent.hasExtra(EXTRA_DEEP_LINK)) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.intent.getStringExtra(EXTRA_DEEP_LINK)))
                startActivity(intent)
            } else {
                // val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.deep_link_home) + "career"))
                // startActivity(intent)
                MainActivity.start(requireContext())
            }
            it.finish()
        }
    }
}
