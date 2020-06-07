package co.tuister.uisers.modules.my_career.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.entities.Subject
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentScheduleBinding
import co.tuister.uisers.modules.my_career.MyCareerState
import co.tuister.uisers.modules.my_career.MyCareerViewModel
import co.tuister.uisers.modules.my_career.subjects.subject_details.AddNoteDialogFragment
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ScheduleFragment : BaseFragment(),
    AddSchedulePeriodDialogFragment.AddSchedulePeriodDialogListener,
    ScheduleAdapter.ScheduleListener {

    private lateinit var binding: FragmentScheduleBinding
    private lateinit var viewModel: ScheduleViewModel

    private val sharedViewModel by sharedViewModel<MyCareerViewModel>(from = { requireActivity() })

    private lateinit var adapter: ScheduleAdapter

    private var subjects: List<Subject>? = null

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    private fun initViews() {
        adapter = ScheduleAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ScheduleFragment.adapter
        }
    }

    private fun initViewModel() {
        viewModel = getViewModel()

        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                update(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            sharedViewModel.state.collect {
                update(it)
            }
        }

        viewModel.initialize()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (adapter.list.isEmpty()) {
            sharedViewModel.updateSubjects()
        }
    }

    private fun update(state: BaseState<Any>?) {
        when (state) {
            is ScheduleState.LoadItems -> loadItems(state)
            is ScheduleState.SavePeriod -> resultSavePeriod(state)
            is MyCareerState.LoadSubjects -> {
                if (state.isSuccess()) {
                    subjects = state.data
                }
            }
        }
    }

    private fun loadItems(state: ScheduleState.LoadItems) {
        when {
            state.inProgress() -> {
                // show loading }
            }
            state.isFailure() -> {
                // show error
            }
            else -> {
                state.data?.run {
                    val items: MutableList<Pair<Int?, SchedulePeriod?>> = mutableListOf()
                    this.groupBy { it.day }.forEach { entry ->
                        items.add(Pair(entry.key, null))
                        items.addAll(entry.value.sortedBy { it.initialHour }.map { Pair(null, it) })
                    }
                    adapter.setItems(items)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    fun addNewItem() {
        showScheduleDialog()
    }

    override fun onSavePeriod(schedulePeriod: SchedulePeriod) {
        viewModel.savePeriod(schedulePeriod)
    }

    private fun resultSavePeriod(state: ScheduleState.SavePeriod) {
        if (state.isSuccess()) {
            viewModel.refresh()
        }
    }

    private fun showScheduleDialog(schedulePeriod: SchedulePeriod? = null) {
        AddSchedulePeriodDialogFragment.create(schedulePeriod, subjects, this)
            .show(parentFragmentManager, AddNoteDialogFragment.TAG)
    }

    override fun onClickPeriod(schedulePeriod: SchedulePeriod) {
        showScheduleDialog(schedulePeriod)
    }
}
