package co.tuister.uisers.modules.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.tuister.domain.entities.Event
import co.tuister.domain.entities.FeedAction
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.entities.Task
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentHomeBinding
import co.tuister.uisers.modules.home.HomeViewModel.State
import co.tuister.uisers.modules.main.MainActivity
import kotlinx.android.synthetic.main.fragment_career_subject_add.view.*
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(), HomeAdapter.HomeListener {

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
        if (!this::adapter.isInitialized) {
            adapter = HomeAdapter(this)
        }
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
            is State.LoadHeader -> state.data?.run { addHeader(this) }
            is State.LoadTasks -> state.data?.run { updateListTasks(this) }
            is State.LoadSubjects -> state.data?.run { updateListSubjects(this) }
            is State.LoadCalendar -> state.data?.run { updateListEvents(this) }
            is State.LoadFeed -> state.data?.run { updateListCards(this) }
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    private fun refresh() {
        viewModel.refresh()
    }

    private fun addHeader(homeHeader: HomeHeader) {
        adapter.list.removeAll { it.template == homeHeader.template }
        adapter.addItem(homeHeader)
    }

    private fun updateListSubjects(list: List<SchedulePeriod>?) {
        adapter.list.removeAll { it.template == HomeAdapter.HomeEnum.SCHEDULE }
        adapter.addItem(HomeSchedule(list))
    }

    private fun updateListTasks(list: List<Task>) {
        adapter.list.removeAll { it.template == HomeAdapter.HomeEnum.TASKS }
        adapter.addItem(HomeTasks(list))
    }

    private fun updateListEvents(list: List<Event>) {
        adapter.list.removeAll { it.template == HomeAdapter.HomeEnum.CALENDAR }
        if (list.isNotEmpty()) {
            adapter.addItem(HomeCalendar(list))
        }
    }

    private fun updateListCards(list: List<HomeCard>) {
        adapter.list.removeAll { it.template == HomeAdapter.HomeEnum.CARD }
        if (list.isNotEmpty()) {
            adapter.addItems(list)
        }
    }

    override fun onClickRow(position: Int, action: FeedAction?) {
        if (action != null) {
            if (!action.deepLink.isNullOrBlank()) {
                mainActivity()?.manageDeepLink(Uri.parse(action.deepLink))
            } else if (!action.url.isNullOrEmpty()) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(action.url)))
            }
        }
    }

    private fun mainActivity(): MainActivity? = activity as? MainActivity
}
