package co.tuister.uisers.modules.institutional.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.postDelayed
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import co.netguru.sectionsDecorator.SectionDecorator
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentCalendarBinding
import co.tuister.uisers.modules.institutional.calendar.CalendarViewModel.State
import co.tuister.uisers.utils.DateUtils
import jp.co.recruit_mp.android.lightcalendarview.LightCalendarView
import jp.co.recruit_mp.android.lightcalendarview.MonthView
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.getViewModel
import java.util.*
import kotlin.math.abs

class CalendarFragment : BaseFragment() {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var viewModel: CalendarViewModel

    private lateinit var adapter: CalendarAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val listener: CalendarAdapter.CalendarListener? = null

    private lateinit var currentDate: Date
    private var updatingDate = false

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater)
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
            addItemDecoration(SectionDecorator(requireContext()).apply {
                setLineColor(R.color.white)
                setLineWidth(1f)
            })
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
                goToDate(date)
            }

            override fun onMonthSelected(date: Date, view: MonthView) {
                goToDate(date)
            }
        })
    }

    private fun checkMonthLabel() {
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        adapter.list.getOrNull(firstVisiblePosition)?.run {
            updatingDate = true
            binding.viewCalendar.setSelectedDate(Date(date))
        }
    }

    private fun goToDate(date: Date) {

        currentDate = date

        val month = DateUtils.dateToString(date, "MMM yyyy")
        if (month != binding.textViewCalendarDate.text.toString()) {
            binding.textViewCalendarDate.text = month
            binding.viewCalendar.monthCurrent = date
        }

        if (updatingDate) {
            updatingDate = false
            return
        }

        val closestEvent = adapter.list.minBy { abs(it.date - date.time) }
        closestEvent?.run {
            val position = adapter.list.indexOf(this)
            layoutManager.scrollToPositionWithOffset(position, 100)
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

    private fun loadItems(state: CalendarViewModel.State.LoadItems) {
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
                    binding.recyclerView.postDelayed(200) {
                        goToDate(Calendar.getInstance().time)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}
