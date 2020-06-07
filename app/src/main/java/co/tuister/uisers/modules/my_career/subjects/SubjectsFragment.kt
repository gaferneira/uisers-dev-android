package co.tuister.uisers.modules.my_career.subjects

import android.os.Bundle
import android.util.Log
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
import co.tuister.uisers.modules.my_career.MyCareerState
import co.tuister.uisers.modules.my_career.MyCareerViewModel
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

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubjectsBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    private fun initViews() {
        footerAdapter = FooterAdapter()
        adapter =
            SubjectsAdapter(listener)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MergeAdapter(this@SubjectsFragment.adapter, footerAdapter)
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

    private fun update(state: BaseState<Any>?) {
        Log.d("GABRIEL", "update state SubjectsFragment")
        Log.d("GABRIEL", state?.toString() ?: "")

        when (state) {
            is MyCareerState.LoadSubjects -> loadItems(state)
            is SubjectsState.LoadSemesterAverage -> {
                footerAdapter.setData(R.string.text_semester_average, state.data)
            }
        }
    }

    private fun loadItems(state: MyCareerState.LoadSubjects) {
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
        // viewModel.refresh()
    }
}
