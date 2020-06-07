package co.tuister.uisers.modules.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.entities.Task
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class HomeFragment : BaseFragment(), HomeAdapter.HomeListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    private lateinit var adapter: HomeAdapter

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    private fun initViews() {
        adapter = HomeAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeFragment.adapter
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

    private fun update(state: BaseState<Any>) {
        when (state) {
            is HomeState.LoadHeader -> state.data?.run { adapter.addItem(this) }
            is HomeState.LoadTasks -> state.data?.run { updateListTasks(this) }
            is HomeState.LoadSubjects -> state.data?.run { updateListSubjects(this) }
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        adapter.clearItems()
        viewModel.refresh()
    }

    private fun updateListSubjects(list: List<SchedulePeriod>?) {
        adapter.addItem(HomeSchedule(list))
    }

    private fun updateListTasks(list: List<Task>) {
        adapter.addItem(HomeTasks(list))
    }

    override fun onClickRow(position: Int) {
    }

    override fun onClickSchedulePeriod(schedulePeriod: SchedulePeriod) {
    }

    override fun onClickTask(task: Task) {
    }
}
