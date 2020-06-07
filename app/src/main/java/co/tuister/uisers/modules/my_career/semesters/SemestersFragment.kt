package co.tuister.uisers.modules.my_career.semesters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.tuister.domain.entities.Semester
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentSemestersBinding
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class SemestersFragment : BaseFragment(), SemestersAdapter.SemesterListener,
    AddSemesterDialogFragment.AddSemesterDialogListener {

    private lateinit var binding: FragmentSemestersBinding
    private lateinit var viewModel: SemestersViewModel

    private lateinit var adapter: SemestersAdapter

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSemestersBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
    private fun initViews() {
        adapter = SemestersAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@SemestersFragment.adapter
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
            is SemestersState.LoadItems -> loadItems(state)
            is SemestersState.SaveSemester -> resultSaveSemester(state)
            is SemestersState.ChangeCurrentSemester -> resultChangeCurrentSemester(state)
        }
    }

    private fun loadItems(state: SemestersState.LoadItems) {
        when {
            state.inProgress() -> {
                // show loading }
            }
            state.isFailure() -> {
                // show error
            }
            else -> {
                state.data?.run {
                    adapter.setItems(this)
                }
            }
        }
    }

    private fun resultSaveSemester(state: SemestersState.SaveSemester) {
        if (state.isSuccess()) {
            viewModel.refresh()
        }
    }

    private fun resultChangeCurrentSemester(state: SemestersState.ChangeCurrentSemester) {
        if (state.isSuccess()) {
            viewModel.refresh()
        }
    }

    override fun onClickSemester(semester: Semester) {
        showDialog(R.string.confirm_change_current_semester, R.string.title_my_career_semesters) {
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
}
