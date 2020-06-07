package co.tuister.uisers.modules.institutional.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentMapBinding
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class MapFragment : BaseFragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var viewModel: MapViewModel

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    private fun initViews() {
    }

    private fun initViewModel() {
        viewModel = getViewModel()
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                update(it)
            }
        }
        viewModel.initialize()
    }

    private fun update(status: BaseState<Any>?) {
        when (status) {
        }
    }
}
