package co.tuister.uisers.modules.institutional

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentInstitutionalBinding
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class InstitutionalFragment : BaseFragment(), InstitutionalAdapter.InstitutionalListener {

    private lateinit var adapter: InstitutionalAdapter

    private lateinit var binding: FragmentInstitutionalBinding
    private lateinit var viewModel: InstitutionalViewModel

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstitutionalBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    private fun initViews() {
        adapter = InstitutionalAdapter(this)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@InstitutionalFragment.adapter
        }
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

    private fun update(state: BaseState<Any>?) {
        when (state) {
            is InstitutionalState.LoadItems -> loadItems(state)
        }
    }

    private fun loadItems(state: InstitutionalState.LoadItems) {
        when {
            state.inProgress() -> {
                // show loading }
            }
            state.isFailure() -> {
                // show error
            }
            else -> {
                state.data?.run {
                    adapter.setItems(this)
                }
            }
        }
    }

    override fun onClickMenu(position: Int) {
        val action = when (position) {
            0 -> R.id.action_institutional_to_map
            1 -> R.id.action_institutional_to_calendar
            2 -> R.id.action_institutional_to_wheels
            else -> R.id.action_institutional_to_map
        }
        findNavController().navigate(action)
    }
}
