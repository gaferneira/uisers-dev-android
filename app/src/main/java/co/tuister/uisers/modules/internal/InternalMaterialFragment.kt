package co.tuister.uisers.modules.internal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.databinding.FragmentInternalMaterialBinding
import kotlinx.android.synthetic.main.fragment_internal_material.view.*

class InternalMaterialFragment : BaseFragment() {

    private lateinit var binding: FragmentInternalMaterialBinding

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        requireContext().theme.applyStyle(theme, true)
        binding = FragmentInternalMaterialBinding.inflate(inflater)
        initViews()
        return binding.root
    }

    private fun initViews() {

        val checkId = if (theme == R.style.Base_Theme_MyApp) R.id.radio_button_one
        else R.id.radio_button_two
        binding.radioGroup.check(checkId)

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            theme = if (checkedId == R.id.radio_button_one) {
                R.style.Base_Theme_MyApp
            } else {
                R.style.ThemeOverlay_MyApp_Light
            }
            showDialog("Ingrese de nuevo para ver el cambio de theme", "") {
                findNavController().popBackStack()
            }
        }
    }

    companion object {
        var theme = R.style.Base_Theme_MyApp
    }
}
