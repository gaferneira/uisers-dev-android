package co.tuister.uisers.modules.institutional.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.postDelayed
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentInstitutionalCalendarBinding
import co.tuister.uisers.modules.institutional.calendar.CalendarViewModel.State
import co.tuister.uisers.utils.DateUtils
import co.tuister.uisers.utils.sectionsDecorator.SectionDecorator
import jp.co.recruit_mp.android.lightcalendarview.LightCalendarView
import jp.co.recruit_mp.android.lightcalendarview.MonthView
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel
import java.util.Calendar
import java.util.Date
import kotlin.math.abs

class CalendarFragment : BaseFragment<FragmentInstitutionalCalendarBinding>(), MotionLayout.TransitionListener {

    private lateinit var viewModel: CalendarViewModel

    private lateinit var adapter: CalendarAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val listener: CalendarAdapter.CalendarListener? = null

    private lateinit var currentDate: Date
    private var updatingDate = false
    private var updatingMonthDate = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstitutionalCalendarBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    private fun initViews() {
        adapter = CalendarAdapter(listener)
        layoutManager = LinearLayoutManager(context)
        binding.recyclerView.apply {
            layoutManager = this@CalendarFragment.layoutManager
            adapter = this@CalendarFragment.adapter
            addItemDecoration(
                SectionDecorator(requireContext()).apply {
                    setLineColor(R.color.white)
                    setLineWidth(1f)
                }
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                private var hasStarted = false

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (!hasStarted && newState == SCROLL_STATE_DRAGGING) {
                        hasStarted = true
                    }

                    if (hasStarted && newState == SCROLL_STATE_IDLE) {
                        hasStarted = false
                        checkMonthLabel()
                    }
                }
            })
        }

        binding.viewCalendar.setOnStateUpdatedListener(object : LightCalendarView.OnStateUpdatedListener {
            override fun onDateSelected(date: Date) {
                if (updatingDate) {
                    updatingDate = false
                    return
                }
                goToDate(date)
            }

            override fun onMonthSelected(date: Date, view: MonthView) {
                if (updatingMonthDate) {
                    updatingMonthDate = false
                    return
                }
                goToDate(date)
            }
        })

        binding.motionLayout.setTransitionListener(this)
    }

    private fun checkMonthLabel() {
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        adapter.list.getOrNull(firstVisiblePosition)?.run {
            if (updateMonthLabel(this.date)) {
                updatingDate = true
                updatingMonthDate = true
                binding.viewCalendar.setSelectedDate(this.date)
            }
        }
    }

    private fun updateMonthLabel(date: Date): Boolean {
        val month = DateUtils.dateToString(date, "MMM yyyy")
        return if (month != binding.textViewCalendarDate.text.toString()) {
            binding.textViewCalendarDate.text = month
            binding.viewCalendar.monthCurrent = date
            true
        } else false
    }

    private fun goToDate(date: Date) {
        currentDate = date
        updateMonthLabel(date)
        val closestEvent = adapter.list.minBy { abs(it.date.time - date.time) }
        closestEvent?.run {
            val position = adapter.list.indexOf(this)
            layoutManager.scrollToPositionWithOffset(position, OFFSET_SCROLL)
        }
    }

    private fun initViewModel() {
        viewModel = getViewModel()
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                update(it)
            }
        }
    }

    private fun update(state: BaseState<Any>?) {
        when (state) {
            is State.LoadItems -> loadItems(state)
        }
    }

    private fun loadItems(state: State.LoadItems) {
        handleState(state) {
            state.data?.run {
                adapter.setItems(this)
                binding.recyclerView.postDelayed(LOAD_DELAY) {
                    goToDate(Calendar.getInstance().time)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    companion object {
        private const val OFFSET_SCROLL = 100
        private const val LOAD_DELAY: Long = 200
    }

    // Transition Listener

    override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerdId: Int, positive: Boolean, progress: Float) {
        // No op
    }

    override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
        // No op
    }

    override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
        // No op
    }

    override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
        val enable = currentId == motionLayout?.startState
        motionLayout?.getTransition(R.id.scroll_transition)?.setEnable(enable)
    }
}
