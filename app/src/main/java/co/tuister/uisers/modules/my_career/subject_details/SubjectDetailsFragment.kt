package co.tuister.uisers.modules.my_career.subject_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import co.tuister.domain.entities.Note
import co.tuister.domain.entities.Subject
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentSubjectDetailsBinding
import co.tuister.uisers.modules.my_career.FooterAdapter
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class SubjectDetailsFragment : BaseFragment(), NotesAdapter.NoteListener {

    private lateinit var adapter: NotesAdapter
    private lateinit var footerAdapter: FooterAdapter

    private lateinit var binding: FragmentSubjectDetailsBinding
    private lateinit var viewModel: SubjectDetailsViewModel

    private lateinit var subject: Subject

    private val safeArgs by navArgs<SubjectDetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadArguments()
    }

    private fun loadArguments() {
        subject = safeArgs.subject
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubjectDetailsBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    private fun initViews() {
        footerAdapter = FooterAdapter()
        adapter = NotesAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MergeAdapter(this@SubjectDetailsFragment.adapter, footerAdapter)
        }
        binding.buttonAdd.setOnClickListener {
            // show alert
        }
    }

    private fun initViewModel() {
        viewModel = getViewModel()
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                update(it)
            }
        }
        viewModel.initialize(subject)
    }

    private fun update(state: BaseState<Any>?) {
        when (state) {
            is SubjectDetailsState.LoadItems -> loadItems(state)
        }
    }

    private fun loadItems(state: SubjectDetailsState.LoadItems) {
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
                    val total = this.map { it.total }.sum()
                    footerAdapter.setData(R.string.text_final_grade, total)
                }
            }
        }
    }

    override fun onClickNote(note: Note) {
        // show dialog
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}
