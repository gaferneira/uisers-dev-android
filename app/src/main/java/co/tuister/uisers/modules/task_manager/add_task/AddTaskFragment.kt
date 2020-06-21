package co.tuister.uisers.modules.task_manager.add_task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.tuister.domain.entities.Task
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentTasksAddBinding
import co.tuister.uisers.modules.task_manager.add_task.AddTaskViewModel.State
import co.tuister.uisers.utils.DateUtils
import co.tuister.uisers.utils.extensions.checkRequireFormFields
import co.tuister.uisers.utils.extensions.pickDateTime
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel
import java.util.*

class AddTaskFragment : BaseFragment() {

    private lateinit var binding: FragmentTasksAddBinding
    private lateinit var viewModel: AddTaskViewModel

    private lateinit var task: Task

    private val safeArgs by navArgs<AddTaskFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadArguments()
    }

    private fun loadArguments() {
        task = safeArgs.task ?: Task()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksAddBinding.inflate(inflater)
        binding.taskBinding = task
        initViews()
        initViewModel()
        return binding.root
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

    private fun update(status: BaseState<Any>?) {
        when (status) {
            is State.Save -> onSaveTask(status)
        }
    }

    private fun onSaveTask(state: State.Save) {
        when {
            state.inProgress() -> {
                // show loading }
            }
            state.isFailure() -> {
                // show error
                binding.buttonSave.isEnabled = true
            }
            else -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun initViews() {

        task.reminder?.run {
            binding.editTextReminder.setText(reminderOption(this))
        }

        binding.editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            task.dueDate?.run {
                calendar.timeInMillis = this
            }
            requireContext().pickDateTime(calendar) {
                binding.taskBinding = task.apply {
                    dueDate = it.timeInMillis
                }
            }
        }

        binding.editTextReminder.setOnClickListener {
            showReminderOptions()
        }

        binding.chipStatus1.setOnClickListener {
            updateSeekBar(0)
        }

        binding.chipStatus2.setOnClickListener {
            updateSeekBar(1)
        }

        binding.chipStatus3.setOnClickListener {
            updateSeekBar(2)
        }

        binding.buttonSave.setOnClickListener {
            hideKeyboard()
            if (requireContext().checkRequireFormFields(binding.editTextTitleTask)) {
                it.isEnabled = false
                viewModel.saveTask(task)
            }
        }
    }

    private fun updateSeekBar(progress: Int) {
        binding.taskBinding = task.apply {
            status = progress
        }
    }

    private fun showReminderOptions() {
        val valuesList = listOf(null) +
            List(7) { (it) * 5 } + // minutes
            List(4) { (it + 1) * 60 } + // hours
            List(2) { (it + 1) * 60 * 24 } // days

        val options = valuesList.map { reminderOption(it) }.toTypedArray()

        // setup the alert builder
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Set reminder")
            .setItems(options) { _, which ->
                task.reminder = valuesList[which]
                binding.editTextReminder.setText(options[which])
            }
            .create()

        dialog.show()
    }

    private fun reminderOption(value: Int?): String {
        return when (value) {
            null -> "No reminder"
            0 -> "At time of Due date"
            else -> DateUtils.minutesToString(resources, value) + " before"
        }
    }
}
