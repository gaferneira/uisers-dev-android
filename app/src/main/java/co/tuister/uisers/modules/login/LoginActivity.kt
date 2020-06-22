package co.tuister.uisers.modules.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseActivity
import co.tuister.uisers.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var navController: NavController

    companion object {
        const val EXTRA_DEEP_LINK = "EXTRA_DEEP_LINK"

        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }

        fun createIntent(context: Context, deepLink: String? = null): Intent {
            return Intent(context, LoginActivity::class.java).apply {
                if (!deepLink.isNullOrEmpty()) {
                    putExtra(EXTRA_DEEP_LINK, deepLink)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        navController = findNavController(R.id.fragment_nav_host_login)
    }
}
