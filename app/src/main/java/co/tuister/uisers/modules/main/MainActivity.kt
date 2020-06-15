package co.tuister.uisers.modules.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat.START
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.setupActionBarWithNavController
import co.tuister.domain.entities.User
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseActivity
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.ActivityMainBinding
import co.tuister.uisers.databinding.LeftHomeMenuHeaderLayoutBinding
import co.tuister.uisers.modules.login.LoginActivity
import co.tuister.uisers.modules.main.MainViewModel.State.DownloadedImage
import co.tuister.uisers.modules.main.MainViewModel.State.ValidateLogout
import co.tuister.uisers.modules.profile.ProfileActivity
import co.tuister.uisers.utils.ImagesUtils.Companion.downloadImageInto
import co.tuister.uisers.utils.setupWithNavController
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class MainActivity : BaseActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingMenu: LeftHomeMenuHeaderLayoutBinding
    private lateinit var viewModel: MainViewModel

    companion object {
        private const val EXTRA_USER = "EXTRA_USER"

        fun start(context: Context, user: User?) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_USER, user)
            context.startActivity(intent)
        }
    }

    private var currentNavController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        bindingMenu =
            LeftHomeMenuHeaderLayoutBinding.inflate(layoutInflater, binding.drawerLayout, false)
        viewModel = getViewModel()
        binding.lifecycleOwner = this
        bindingMenu.lifecycleOwner = this
        binding.viewModel = viewModel
        bindingMenu.viewModel = viewModel

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
        initViews()
    }

    override fun onResume() {
        super.onResume()
        viewModel.downloadImage()
        viewModel.downloadUserData()
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.navigationIcon = null

        binding.circleImagePhoto.setOnClickListener {
            binding.drawerLayout.openDrawer(START)
        }

        binding.navigationView.addHeaderView(bindingMenu.root)
        binding.navigationView.setNavigationItemSelectedListener { menu ->
            when (menu.title) {
                resources.getString(R.string.title_menu_profile) -> ProfileActivity.start(
                    this,
                    viewModel.user
                )
                resources.getString(R.string.title_menu_about) -> {
                    // TODO: Implement about
                }
                resources.getString(R.string.title_menu_feedback) -> {
                    // TODO: Implement about
                }
            }
            true
        }
        viewModel.setUserData(intent.getSerializableExtra(EXTRA_USER) as User?)
        viewModel.version.value =
            applicationContext.packageManager.getPackageInfo(packageName, 0).versionName
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                update(it)
            }
        }
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {

        val navGraphIds = listOf(R.navigation.nav_graph_home,
            R.navigation.nav_graph_my_career,
            R.navigation.nav_graph_task_manager,
            R.navigation.nav_graph_institution
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = binding.bottomNavView.setupWithNavController(
            navGraphIds,
            supportFragmentManager,
            R.id.nav_host_container,
            intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            setupActionBarWithNavController(navController)
            currentNavController?.removeOnDestinationChangedListener(this)
            currentNavController = navController
            currentNavController?.addOnDestinationChangedListener(this)
        })
    }

    override fun onDestinationChanged(
      controller: NavController,
      destination: NavDestination,
      arguments: Bundle?
    ) {
        val backButtonVisible = drawerToggleDelegate?.isNavigationVisible ?: false
        binding.circleImagePhoto.isVisible = !backButtonVisible
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        title?.let {
            viewModel.title.value = it.toString()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.navigateUp() ?: false
    }

    private fun update(state: BaseState<Any>) {
        when (state) {
            is ValidateLogout -> validateLogout(state)
            is DownloadedImage -> downloadImage(state)
        }
    }

    private fun downloadImage(state: DownloadedImage) {
        when {
            state.isSuccess() -> {
                downloadImageInto(this, state.result.data, binding.circleImagePhoto)
                downloadImageInto(this, state.result.data, bindingMenu.circleImagePhoto)
            }
        }
    }

    private fun validateLogout(state: ValidateLogout) {
        when {
            state.inProgress() -> {
                binding.loginStatus.isVisible = true
            }
            state.isSuccess() -> {
                binding.loginStatus.isVisible = false
                finish()
                LoginActivity.start(this)
            }
            else -> {
                binding.loginStatus.isVisible = false
            }
        }
    }
}
