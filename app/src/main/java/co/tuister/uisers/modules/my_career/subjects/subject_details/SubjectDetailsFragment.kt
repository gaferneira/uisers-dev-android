package co.tuister.uisers.modules.my_career.subjects.subject_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
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
import co.tuister.uisers.modules.my_career.subjects.subject_details.SubjectDetailsViewModel.State
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel

class SubjectDetailsFragment :
    BaseFragment(),
    NotesAdapter.NoteListener,
    AddNoteDialogFragment.AddNoteDialogListener {

    private lateinit var adapter: NotesAdapter
    private lateinit var footerAdapter: FooterAdapter

    private lateinit var binding: FragmentSubjectDetailsBinding
    private lateinit var viewModel: SubjectDetailsViewModel

    private lateinit var subject: Subject

    private val safeArgs by navArgs<SubjectDetailsFragmentArgs>()

    override fun getTitle() = subject.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadArguments()
        initViewModel()
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
        return binding.root
    }

    private fun initViews() {
        if (!this::footerAdapter.isInitialized) {
            footerAdapter = FooterAdapter()
        }
        if (!this::adapter.isInitialized) {
            adapter = NotesAdapter(this)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MergeAdapter(this@SubjectDetailsFragment.adapter, footerAdapter)
        }
        binding.buttonAdd.setOnClickListener {
            showNoteDialog()
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
            is State.LoadItems -> loadItems(state)
            is State.SaveNote -> resultSaveNote(state)
            is State.RemoveItem -> removeItem(state)
            is State.LoadAverage -> getAverage(state)
        }
    }

    private fun loadItems(state: State.LoadItems) {
        handleState(state) {
            it?.run {
                adapter.setItems(this)
            }
        }
    }

    private fun getAverage(state: State.LoadAverage) {
        handleState(state) {
            footerAdapter.setData(R.string.text_final_grade, state.data)
        }
    }

    private fun resultSaveNote(state: State.SaveNote) {
        handleState(state) {
            viewModel.refresh()
        }
    }

    override fun onClickNote(note: Note) {
        showNoteDialog(note)
    }

    private fun showNoteDialog(note: Note? = null) {
        val maxPercentage =
            adapter.list.filterNot { it == note }.sumByDouble { it.percentage.toDouble() }.let {
                val value = 100 - it.toFloat()
                if (value > 0) {
                    value
                } else {
                    0f
                }
            }

        AddNoteDialogFragment.create(subject, note, maxPercentage.toFloat(), this)
            .show(parentFragmentManager, AddNoteDialogFragment.TAG)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onSaveNote(note: Note) {
        viewModel.saveNote(note)
    }

    private fun removeItem(state: State.RemoveItem) {
        handleState(state) {
            viewModel.refresh()
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val adapterPosition = item.groupId
        val note = adapter.list[adapterPosition]
        showConfirmDialog(getString(R.string.confirm_remove_note), note.title) {
            viewModel.removeNote(note)
        }
        return true
    }
}
