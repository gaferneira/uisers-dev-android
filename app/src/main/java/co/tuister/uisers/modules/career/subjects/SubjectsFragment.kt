package co.tuister.uisers.modules.career.subjects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentCareerSubjectsBinding
import co.tuister.uisers.modules.career.FooterAdapter
import co.tuister.uisers.modules.career.subjects.SubjectsViewModel.State
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class SubjectsFragment : BaseFragment<FragmentCareerSubjectsBinding>() {

    private lateinit var adapter: SubjectsAdapter
    private lateinit var footerAdapter: FooterAdapter

    private lateinit var viewModel: SubjectsViewModel

    var listener: SubjectsAdapter.SubjectListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCareerSubjectsBinding.inflate(inflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        if (!this::adapter.isInitialized) {
            adapter = SubjectsAdapter()
        }
        adapter.listener = listener
        if (!this::footerAdapter.isInitialized) {
            footerAdapter = FooterAdapter()
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ConcatAdapter(this@SubjectsFragment.adapter, footerAdapter)
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
            is State.LoadSubjects -> loadItems(state)
            is State.RemoveSubject -> removeItem(state)
            is State.LoadSemesterAverage -> {
                footerAdapter.setData(R.string.career_label_text_semester_average, state.data)
            }
        }
    }

    private fun loadItems(state: State.LoadSubjects) {
        handleState(state) {
            it?.run {
                adapter.setItems(this)
                binding.textViewNoData.isVisible = adapter.list.isEmpty()
            }
        }
    }

    private fun removeItem(state: State.RemoveSubject) {
        handleState(state) {
            viewModel.refresh()
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    fun refresh() {
        viewModel.refresh()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (lifecycle.currentState != Lifecycle.State.RESUMED || !isVisible) return false

        val adapterPosition = item.groupId
        val subject = adapter.list.getOrNull(adapterPosition)
        subject?.run {
            if (item.itemId == 0) {
                listener?.onClickEdit(subject)
            } else {
                showConfirmDialog(getString(R.string.career_confirm_remove_subject), name) {
                    viewModel.removeSubject(this)
                }
            }
        }
        return true
    }
}
