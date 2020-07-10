package co.tuister.uisers.modules.career.semesters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.tuister.domain.entities.Semester
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentCareerSemestersBinding
import co.tuister.uisers.modules.career.FooterAdapter
import co.tuister.uisers.modules.career.semesters.SemestersViewModel.State
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class SemestersFragment :
    BaseFragment<FragmentCareerSemestersBinding>(),
    SemestersAdapter.SemesterListener,
    AddSemesterDialogFragment.AddSemesterDialogListener {

    private lateinit var viewModel: SemestersViewModel

    private lateinit var adapter: SemestersAdapter
    private lateinit var footerAdapter: FooterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCareerSemestersBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
    private fun initViews() {
        if (!this::adapter.isInitialized) {
            adapter = SemestersAdapter(this)
        }
        if (!this::footerAdapter.isInitialized) {
            footerAdapter = FooterAdapter()
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ConcatAdapter(this@SemestersFragment.adapter, footerAdapter)
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
            is State.SaveSemester -> resultSaveSemester(state)
            is State.ChangeCurrentSemester -> resultChangeCurrentSemester(state)
            is State.RemoveItem -> removeItem(state)
        }
    }

    private fun loadItems(state: State.LoadItems) {
        handleState(state) { data ->
            data?.run {
                adapter.setItems(this)

                val credits = map { it.credits }.sum()
                val average = if (credits != 0) {
                    map { it.average * it.credits }.sum() / credits
                } else 0f

                footerAdapter.setData(R.string.career_label_semesters_average, average)
            }
        }
    }

    private fun resultSaveSemester(state: State.SaveSemester) {
        handleState(state) {
            viewModel.refresh()
        }
    }

    private fun resultChangeCurrentSemester(state: State.ChangeCurrentSemester) {
        handleState(state) {
            viewModel.refresh()
        }
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

    override fun onClickSemester(semester: Semester) {
        if (semester.current) return
        showConfirmDialog(R.string.career_confirm_change_current_semester, R.string.career_title_semesters) {
            viewModel.changeCurrentSemester(semester)
        }
    }

    fun showSemesterDialog() {
        AddSemesterDialogFragment.create(adapter.list, this)
            .show(parentFragmentManager, AddSemesterDialogFragment.TAG)
    }

    override fun onSaveSemester(semester: Semester) {
        viewModel.saveSemester(semester)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (lifecycle.currentState != Lifecycle.State.RESUMED || !isVisible) return false

        val adapterPosition = item.groupId
        val semester = adapter.list.getOrNull(adapterPosition) ?: return false

        if (semester.current) {
            displayMessage(R.string.career_alert_remove_current_semester, R.string.base_action_remove, 1)
            return false
        }

        showConfirmDialog(getString(R.string.career_confirm_remove_semester), semester.period) {
            viewModel.removeSemester(semester)
        }
        return true
    }
}
