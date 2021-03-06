package co.tuister.uisers.modules.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentTasksListBinding
import co.tuister.uisers.modules.tasks.TasksViewModel.State
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.sharedViewModel

class TasksListFragment : BaseFragment<FragmentTasksListBinding>() {

    private lateinit var adapter: TasksAdapter

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
            is State.LoadItems -> loadItems(state)
            is State.RemoveItem -> removeItem(state)
        }
    }

    private fun loadItems(state: State.LoadItems) {
        handleState(state) {
            state.data?.run {
                adapter.setItems(this.filter { it.status == status })
                binding.textViewTasksNoData.isVisible = adapter.list.isEmpty()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    private fun removeItem(state: State.RemoveItem) {
        handleState(state) {
            viewModel.refresh()
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (lifecycle.currentState != Lifecycle.State.RESUMED || !isVisible) return false
        val adapterPosition = item.groupId
        val task = adapter.list.getOrNull(adapterPosition)
        task?.run {
            showConfirmDialog(getString(R.string.tasks_confirm_remove_task), title) {
                viewModel.remove(this)
            }
        }
        return true
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
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
