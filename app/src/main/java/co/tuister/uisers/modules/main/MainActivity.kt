package co.tuister.uisers.modules.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat.START
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import co.tuister.domain.entities.User
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseActivity
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.ActivityMainBinding
import co.tuister.uisers.databinding.LeftHomeMenuHeaderLayoutBinding
import co.tuister.uisers.modules.login.LoginActivity
import co.tuister.uisers.modules.main.MainState.ValidateLogout
import co.tuister.uisers.modules.profile.ProfileActivity
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var bindingMenu: LeftHomeMenuHeaderLayoutBinding
    lateinit var viewModel: MainViewModel
    private lateinit var navController: NavController

    companion object {
        private val EXTRA_USER = "EXTRA_USER"

        fun start(context: Context, user: User?) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_USER, user)
            context.startActivity(intent)
        }
    }

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
        navController = findNavController(R.id.fragment_nav_host_home)
        initViews()
    }

    private fun initViews() {
        binding.bottomNavView.setOnNavigationItemSelectedListener {
            onNavDestinationSelected(it, navController)
            viewModel.title.value = it.title.toString()
            true
        }
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

    private fun update(state: BaseState<Any>) {
        when (state) {
            is ValidateLogout -> validateLogout(state)
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