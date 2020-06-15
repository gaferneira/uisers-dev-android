package co.tuister.uisers.modules.my_career.subjects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentSubjectsBinding
import co.tuister.uisers.modules.my_career.FooterAdapter
import co.tuister.uisers.modules.my_career.MyCareerViewModel
import co.tuister.uisers.modules.my_career.subjects.SubjectsViewModel.State
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SubjectsFragment : BaseFragment() {

    private lateinit var adapter: SubjectsAdapter
    private lateinit var footerAdapter: FooterAdapter

    private lateinit var binding: FragmentSubjectsBinding
    private lateinit var viewModel: SubjectsViewModel

    private val sharedViewModel by sharedViewModel<MyCareerViewModel>(from = { requireActivity() })

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
        binding = FragmentSubjectsBinding.inflate(inflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        if (!this::adapter.isInitialized) {
            adapter = SubjectsAdapter(listener)
        }
        if (!this::footerAdapter.isInitialized) {
            footerAdapter = FooterAdapter()
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MergeAdapter(this@SubjectsFragment.adapter, footerAdapter)
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

        lifecycleScope.launchWhenStarted {
            sharedViewModel.state.collect {
                update(it)
            }
        }
    }

    private fun update(state: BaseState<Any>?) {
        when (state) {
            is MyCareerViewModel.State.LoadSubjects -> loadItems(state)
            is State.LoadSemesterAverage -> {
                footerAdapter.setData(R.string.text_semester_average, state.data)
            }
        }
    }

    private fun loadItems(state: MyCareerViewModel.State.LoadSubjects) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (adapter.list.isEmpty()) {
            sharedViewModel.updateSubjects()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}
