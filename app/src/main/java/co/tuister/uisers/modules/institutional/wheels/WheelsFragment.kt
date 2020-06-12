package co.tuister.uisers.modules.institutional.wheels

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.databinding.FragmentWheelsBinding

class WheelsFragment : BaseFragment() {

    private lateinit var binding: FragmentWheelsBinding

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWheelsBinding.inflate(inflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.buttonOpen.setOnClickListener {
            val appPackageName = "org.co.wheels"
            try {
                val intent =
                    context?.packageManager?.getLaunchIntentForPackage(appPackageName)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }
    }
}
