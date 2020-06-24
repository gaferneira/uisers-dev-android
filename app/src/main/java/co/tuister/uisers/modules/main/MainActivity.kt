package co.tuister.uisers.modules.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.animation.AnimationUtils.loadAnimation
import androidx.core.view.GravityCompat.START
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.setupActionBarWithNavController
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseActivity
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.ActivityMainBinding
import co.tuister.uisers.databinding.LeftHomeMenuHeaderLayoutBinding
import co.tuister.uisers.modules.login.LoginActivity
import co.tuister.uisers.modules.login.register.RegisterFragment
import co.tuister.uisers.modules.main.MainViewModel.State.DownloadedImage
import co.tuister.uisers.modules.main.MainViewModel.State.FirsTime
import co.tuister.uisers.modules.main.MainViewModel.State.ValidateLogout
import co.tuister.uisers.modules.profile.ProfileActivity
import co.tuister.uisers.utils.analytics.Analytics
import co.tuister.uisers.utils.extensions.launchImagePicker
import co.tuister.uisers.utils.extensions.setImageFromUri
import co.tuister.uisers.utils.extensions.setupWithNavController
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class MainActivity :
    BaseActivity(),
    NavController.OnDestinationChangedListener,
    FeedbackDialogFragment.FeedbackDialogListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingMenu: LeftHomeMenuHeaderLayoutBinding
    private lateinit var viewModel: MainViewModel

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
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
        viewModel.downloadUserData()
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.navigationIcon = null

        binding.circleImagePhoto.setOnClickListener {
            analytics.trackEvent(Analytics.EVENT_CLICK_DRAWER_MENU)
            binding.drawerLayout.openDrawer(START)
            viewModel.disableFirstTime()
        }

        bindingMenu.circleImagePhoto.setOnClickListener {
            launchImagePicker()
            stopAnimatedIconsFirstTime()
        }

        binding.navigationView.addHeaderView(bindingMenu.root)
        binding.navigationView.setNavigationItemSelectedListener { menu ->
            when (menu.title) {
                resources.getString(R.string.title_menu_profile) -> {
                    ProfileActivity.start(
                        this,
                        viewModel.user
                    )
                    analytics.trackEvent(Analytics.EVENT_CLICK_PROFILE)
                }
                resources.getString(R.string.title_menu_about) -> {
                    AboutDialogFragment.create()
                        .show(supportFragmentManager, AboutDialogFragment.TAG)
                    binding.drawerLayout.closeDrawers()
                    analytics.trackEvent(Analytics.EVENT_CLICK_ABOUT)
                }
                resources.getString(R.string.title_menu_feedback) -> {
                    FeedbackDialogFragment.create(this)
                        .show(supportFragmentManager, FeedbackDialogFragment.TAG)
                    binding.drawerLayout.closeDrawers()
                    analytics.trackEvent(Analytics.EVENT_CLICK_FEEDBACK)
                }
            }
            true
        }

        viewModel.version.value =
            applicationContext.packageManager.getPackageInfo(packageName, 0).versionName
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                update(it)
            }
        }

        viewModel.checkAnimationFirstTime()
    }

    private fun animateIconsFirstTime() {
        val animation = loadAnimation(
            this,
            R.anim.scale
        )

        binding.circleImagePhoto.startAnimation(animation)
        bindingMenu.circleImagePhoto.startAnimation(animation)
    }

    private fun stopAnimatedIconsFirstTime() {
        binding.circleImagePhoto.clearAnimation()
        bindingMenu.circleImagePhoto.clearAnimation()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {

        val navGraphIds = listOf(
            R.navigation.nav_graph_home,
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
        controller.observe(
            this,
            Observer { navController ->
                setupActionBarWithNavController(navController)

                if (currentNavController != null) {
                    val event = when (navController.graph.id) {
                        R.id.nav_graph_home -> Analytics.EVENT_CLICK_HOME
                        R.id.nav_graph_my_career -> Analytics.EVENT_CLICK_MY_CAREER
                        R.id.nav_graph_task_manager -> Analytics.EVENT_CLICK_TASKS
                        R.id.nav_graph_institution -> Analytics.EVENT_CLICK_INSTITUTION
                        else -> null
                    }
                    if (event != null) {
                        analytics.trackEvent(event)
                    }
                }
                currentNavController?.removeOnDestinationChangedListener(this)
                currentNavController = navController
                currentNavController?.addOnDestinationChangedListener(this)
            }
        )

        // Deep links tabs
        intent.data?.run {
            manageDeeLinkTabs(this)
        }
    }

    private fun manageDeeLinkTabs(uri: Uri) {
        if (uri.pathSegments?.size == 1) {
            val itemId = when (uri.pathSegments.first()) {
                "career" -> R.id.nav_graph_my_career
                "tasks" -> R.id.nav_graph_task_manager
                "institution" -> R.id.nav_graph_institution
                else -> null
            }
            itemId?.let {
                binding.bottomNavView.selectedItemId = it
            }
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val backButtonVisible = drawerToggleDelegate?.isNavigationVisible ?: false
        binding.circleImagePhoto.isVisible = !backButtonVisible
        if (backButtonVisible) {
            binding.circleImagePhoto.clearAnimation()
        }
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
            is FirsTime -> {
                handleState(state) {
                    if (it == true) {
                        animateIconsFirstTime()
                    } else {
                        stopAnimatedIconsFirstTime()
                    }
                }
            }
        }
    }

    private fun downloadImage(state: DownloadedImage) {
        handleState(state) {
            binding.circleImagePhoto.setImageFromUri(state.result.data)
            bindingMenu.circleImagePhoto.setImageFromUri(state.result.data)
        }
    }

    private fun validateLogout(state: ValidateLogout) {
        handleState(
            state,
            inProgress = {
                binding.loginStatus.isVisible = true
            },
            onError = {
                binding.loginStatus.isVisible = false
            },
            onSuccess = {
                binding.loginStatus.isVisible = false
                analytics.trackUserLogout()
                finish()
                LoginActivity.start(this)
            }
        )
    }

    override fun onSendFeedback(feedback: String) {
        viewModel.sendFeedback(feedback)
        showDialog(R.string.feedback_result_message, R.string.feedback_result_title)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RegisterFragment.RESULT_LOAD_IMAGE -> {
                    val uri = data?.data
                    CropImage.activity(uri)
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(true)
                        .start(this)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    viewModel.uploadImage(result.uri)
                    binding.circleImagePhoto.setImageURI(result.uri)
                    bindingMenu.circleImagePhoto.setImageURI(result.uri)
                }
            }
        }
    }
}
