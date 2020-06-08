package co.tuister.uisers.modules.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import co.tuister.uisers.common.BaseActivity
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentProfileBinding
import co.tuister.uisers.modules.login.register.RegisterFragment
import co.tuister.uisers.modules.main.MainState.DownloadedImage
import co.tuister.uisers.utils.ImagesUtils.Companion.downloadImageInto
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private val safeArgs by navArgs<ProfileFragmentArgs>()

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    private fun initViews() {
        binding.buttonUploadPicture.setOnClickListener {
            launchImagePicker()
        }
    }

    private fun initViewModel() {
        viewModel = getViewModel()
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                update(it)
            }
        }
        viewModel.initialize(safeArgs.EXTRAUSER)
        binding.viewModel = viewModel
        binding.activity = activity as BaseActivity
    }

    private fun update(status: BaseState<Any>?) {
        when (status) {
            is DownloadedImage -> downloadImage(status)
        }
    }

    private fun downloadImage(state: DownloadedImage) {
        when {
            state.isSuccess() -> {
                downloadImageInto(requireContext(), state.result.data, binding.circleImagePhoto)
            }
        }
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
                        .start(requireContext(), this)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    viewModel.uploadImage(result.uri)
                    binding.circleImagePhoto.setImageURI(result.uri)
                }
            }
        }
    }
}
