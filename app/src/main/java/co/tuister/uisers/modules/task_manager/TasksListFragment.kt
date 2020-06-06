package co.tuister.uisers.modules.task_manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentTasksListBinding
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.sharedViewModel

class TasksListFragment : BaseFragment() {

    private lateinit var adapter: TasksAdapter

    private lateinit var binding: FragmentTasksListBinding
    private val viewModel by sharedViewModel<TasksViewModel>(from = { requireActivity() })

    private var status: Int = 0

    var listener: TasksAdapter.TasksListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            status = it.getInt(ARG_STATUS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksListBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    private fun initViews() {
        adapter = TasksAdapter(listener)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@TasksListFragment.adapter
        }
    }

    private fun initViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                update(it)
            }
        }
    }

    private fun update(state: BaseState<Any>?) {
        when (state) {
            is TasksState.LoadItems -> loadItems(state)
        }
    }

    private fun loadItems(state: TasksState.LoadItems) {
        when {
            state.inProgress() -> {
                // show loading }
            }
            state.isFailure() -> {
                // show error
            }
            else -> {
                state.data?.run {
                    adapter.setItems(this.filter { it.status == status })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    companion object {

        private const val ARG_STATUS = "status"

        @JvmStatic
        fun newInstance(status: Int) =
            TasksListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_STATUS, status)
                }
            }
    }
}
