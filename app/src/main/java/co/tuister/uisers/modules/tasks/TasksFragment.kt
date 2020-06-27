package co.tuister.uisers.modules.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import co.tuister.domain.entities.Task
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.databinding.FragmentTasksBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.sharedViewModel

class TasksFragment : BaseFragment<FragmentTasksBinding>(), TasksAdapter.TasksListener {

    private val viewModel by sharedViewModel<TasksViewModel>(from = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    private fun initViews() {
        binding.pager.apply {
            offscreenPageLimit = 2
            adapter = TasksPagerAdapter()
        }
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> resources.getString(R.string.tasks_label_status_todo).toUpperCase()
                1 -> resources.getString(R.string.tasks_label_status_in_progress).toUpperCase()
                2 -> resources.getString(R.string.tasks_label_status_done).toUpperCase()
                else -> ""
            }
        }.attach()

        binding.buttonAdd.setOnClickListener {
            val action = TasksFragmentDirections.actionTasksToTasksAdd(null)
            findNavController().navigate(action)
        }
    }

    private fun initViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                // No op
            }
        }
        viewModel.initialize()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onClickTask(task: Task) {
        val action = TasksFragmentDirections.actionTasksToTasksAdd(task)
        findNavController().navigate(action)
    }

    private inner class TasksPagerAdapter : FragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment =
            TasksListFragment.newInstance(position).apply {
                listener = this@TasksFragment
            }
    }
}
