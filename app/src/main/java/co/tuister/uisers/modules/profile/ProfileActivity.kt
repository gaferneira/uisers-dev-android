package co.tuister.uisers.modules.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import co.tuister.domain.entities.User
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseActivity
import co.tuister.uisers.databinding.ActivityProfileBinding

class ProfileActivity : BaseActivity() {
    lateinit var binding: ActivityProfileBinding
    private lateinit var navController: NavController
    private lateinit var user: User

    companion object {
        private const val EXTRA_USER = "extraUser"

        fun start(context: Context, user: User) {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra(EXTRA_USER, user)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.lifecycleOwner = this
        navController = findNavController(R.id.fragment_nav_host_profile)
        user = intent.getSerializableExtra(EXTRA_USER) as User
        setNavigation()
    }

    private fun setNavigation() {
        val bundle = Bundle()
        bundle.putSerializable(EXTRA_USER, user)

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph_profile)
        navGraph.startDestination = R.id.profile_fragment_dest
        navController.setGraph(navGraph, bundle)
    }
}
