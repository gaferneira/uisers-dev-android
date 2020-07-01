package co.tuister.uisers.modules.career.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.entities.Subject
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentCareerScheduleBinding
import co.tuister.uisers.modules.career.schedule.ScheduleViewModel.State
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class ScheduleFragment :
    BaseFragment<FragmentCareerScheduleBinding>(),
    AddSchedulePeriodDialogFragment.AddSchedulePeriodDialogListener,
    ScheduleAdapter.ScheduleListener {

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
        binding = FragmentCareerScheduleBinding.inflate(inflater)
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
            is State.RemoveItem -> removeItem(state)
            is State.LoadSubjects -> handleState(state) {
                subjects = state.data
            }
        }
    }

    private fun loadItems(state: State.LoadItems) {
        handleState(state) { list ->
            val items: MutableList<Pair<Int?, SchedulePeriod?>> = mutableListOf()
            list?.groupBy { it.day }?.forEach { entry ->
                items.add(Pair(entry.key, null))
                items.addAll(entry.value.sortedBy { it.initialHour }.map { Pair(null, it) })
            }
            adapter.setItems(items)
            binding.textViewNoData.isVisible = adapter.list.isEmpty()
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
        handleState(state) {
            viewModel.refresh()
        }
    }

    private fun showScheduleDialog(schedulePeriod: SchedulePeriod? = null) {
        AddSchedulePeriodDialogFragment.create(schedulePeriod, subjects, this)
            .show(parentFragmentManager, AddSchedulePeriodDialogFragment.TAG)
    }

    override fun onClickPeriod(period: SchedulePeriod) {
        showScheduleDialog(period)
    }

    private fun removeItem(state: State.RemoveItem) {
        handleState(state) {
            viewModel.refresh()
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (lifecycle.currentState != Lifecycle.State.RESUMED || !isVisible) return false

        val adapterPosition = item.groupId
        val period = adapter.list.getOrNull(adapterPosition)?.second ?: return false
        showConfirmDialog(getString(R.string.career_confirm_remove_period), period.description) {
            viewModel.removePeriod(period)
        }
        return true
    }
}
