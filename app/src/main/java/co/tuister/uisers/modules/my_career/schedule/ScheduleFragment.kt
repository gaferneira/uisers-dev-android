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
import co.tuister.uisers.modules.my_career.schedule.ScheduleViewModel.State
import co.tuister.uisers.modules.my_career.subjects.subject_details.AddNoteDialogFragment
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class ScheduleFragment :
    BaseFragment(),
    AddSchedulePeriodDialogFragment.AddSchedulePeriodDialogListener,
    ScheduleAdapter.ScheduleListener {

    private lateinit var binding: FragmentScheduleBinding
    private lateinit var viewModel: ScheduleViewModel

    private lateinit var adapter: ScheduleAdapter

    private var subjects: List<Subject>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        if (!this::adapter.isInitialized) {
            adapter = ScheduleAdapter(this)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ScheduleFragment.adapter
        }
    }

    private fun initViewModel() {
        viewModel = getViewModel()

        lifecycleScope.launchWhenStarted {
            viewModel.initialize()
            viewModel.state.collect {
                update(it)
            }
        }
    }

    private fun update(state: BaseState<Any>?) {
        when (state) {
            is State.LoadItems -> loadItems(state)
            is State.SavePeriod -> resultSavePeriod(state)
            is State.LoadSubjects -> {
                if (state.isSuccess()) {
                    subjects = state.data
                }
            }
        }
    }

    private fun loadItems(state: State.LoadItems) {
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

    override fun onSavePeriod(period: SchedulePeriod) {
        viewModel.savePeriod(period)
    }

    private fun resultSavePeriod(state: State.SavePeriod) {
        if (state.isSuccess()) {
            viewModel.refresh()
        }
    }

    private fun showScheduleDialog(schedulePeriod: SchedulePeriod? = null) {
        AddSchedulePeriodDialogFragment.create(schedulePeriod, subjects, this)
            .show(parentFragmentManager, AddNoteDialogFragment.TAG)
    }

    override fun onClickPeriod(period: SchedulePeriod) {
        showScheduleDialog(period)
    }
}
