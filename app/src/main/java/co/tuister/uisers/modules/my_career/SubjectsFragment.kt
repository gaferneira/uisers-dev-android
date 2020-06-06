package co.tuister.uisers.modules.my_career

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import co.tuister.domain.entities.Subject
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentSubjectsBinding
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class SubjectsFragment : BaseFragment(), SubjectsAdapter.SubjectListener {

    private lateinit var adapter: SubjectsAdapter
    private lateinit var footerAdapter: FooterAdapter

    private lateinit var binding: FragmentSubjectsBinding
    private lateinit var viewModel: SubjectsViewModel

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
        adapter = SubjectsAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MergeAdapter(this@SubjectsFragment.adapter, footerAdapter)
        }
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(R.id.action_subjects_to_subject_add)
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
            is SubjectsState.LoadItems -> loadItems(state)
            is SubjectsState.LoadSemesterAverage -> {
                footerAdapter.setData(R.string.text_semester_average, state.data)
            }
        }
    }

    private fun loadItems(state: SubjectsState.LoadItems) {
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

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onClickSubject(subject: Subject) {
        val action = SubjectsFragmentDirections.actionSubjectsToSubjectDetails(subject)
        findNavController().navigate(action)
    }
}
