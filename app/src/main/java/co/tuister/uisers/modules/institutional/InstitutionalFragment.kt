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
import co.tuister.uisers.modules.institutional.InstitutionalViewModel.State
import co.tuister.uisers.utils.analytics.Analytics
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
        adapter = InstitutionalAdapter()
        adapter.listener = this
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
            is State.LoadItems -> loadItems(state)
        }
    }

    private fun loadItems(state: State.LoadItems) {
        handleState(state) {
            it?.run {
                adapter.setItems(this)
            }
        }
    }

    override fun onDestroyView() {
        adapter.listener = null
        super.onDestroyView()
    }

    override fun onClickMenu(position: Int) {
        val action = when (position) {
            0 -> Pair(R.id.action_institutional_to_map, Analytics.EVENT_CLICK_MAP)
            1 -> Pair(R.id.action_institutional_to_calendar, Analytics.EVENT_CLICK_CALENDAR)
            2 -> Pair(R.id.action_institutional_to_wheels, Analytics.EVENT_CLICK_WHEELS)
            else -> Pair(R.id.action_institutional_to_map, Analytics.EVENT_CLICK_MAP)
        }

        findNavController().navigate(action.first)
        analytics.trackEvent(action.second)
    }
}
