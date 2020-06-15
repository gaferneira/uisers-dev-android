package co.tuister.uisers.modules.internal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseActivity
import co.tuister.uisers.databinding.ActivityInternalBinding

class InternalActivity : BaseActivity() {
    lateinit var binding: ActivityInternalBinding
    private lateinit var navController: NavController

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, InternalActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_internal)
        binding.lifecycleOwner = this
        navController = findNavController(R.id.fragment_nav_host_internal)
        setNavigation()
    }

    private fun setNavigation() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph_internal)
        navGraph.startDestination = R.id.internal_fragment_dest
        navController.graph = navGraph
    }
}
