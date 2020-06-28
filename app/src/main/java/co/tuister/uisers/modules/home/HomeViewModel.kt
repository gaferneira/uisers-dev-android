package co.tuister.uisers.modules.home

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Event
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.entities.Task
import co.tuister.domain.usecases.GetFeedUseCase
import co.tuister.domain.usecases.career.GetCurrentSemesterUseCase
import co.tuister.domain.usecases.career.GetScheduleByDateUseCase
import co.tuister.domain.usecases.institutional.GetUpcomingEventsUseCase
import co.tuister.domain.usecases.tasks.GetMainTasks
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class HomeViewModel(
    private val getCurrentSemesterCase: GetCurrentSemesterUseCase,
    private val getMainTasks: GetMainTasks,
    private val scheduleByUseCase: GetScheduleByDateUseCase,
    private val upcomingEventsUseCase: GetUpcomingEventsUseCase,
    private val getFeedUseCase: GetFeedUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class LoadHeader(result: Result<HomeHeader>) : State<HomeHeader>(result)
        class LoadTasks(result: Result<List<Task>>) : State<List<Task>>(result)
        class LoadSubjects(result: Result<List<SchedulePeriod>>) : State<List<SchedulePeriod>>(result)
        class LoadCalendar(result: Result<List<Event>>) : State<List<Event>>(result)
        class LoadFeed(result: Result<List<HomeCard>>) : State<List<HomeCard>>(result)
    }

    fun initialize() {
        // No op
    }

    fun refresh() {
        updateLabels()
        updateTasks()
        updateSubjects()
        updateEvents()
        updateFeed()
    }

    private fun updateLabels() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { getCurrentSemesterCase.run() }
            result.fold(
                {
                    // left --> error
                },
                { semester ->
                    val period = semester.period
                    val header = HomeHeader("", period)
                    setState(State.LoadHeader(Result.Success(header)))
                }
            )
        }
    }

    private fun updateTasks() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val result = withContext(Dispatchers.IO) {
                getMainTasks.run(calendar.time)
            }
            result.fold(
                {
                    setState(State.LoadTasks(Result.Error(it)))
                },
                {
                    setState(State.LoadTasks(Result.Success(it)))
                }
            )
        }
    }

    private fun updateSubjects() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val result = withContext(Dispatchers.IO) { scheduleByUseCase.run(calendar.time) }
            result.fold(
                {
                    setState(State.LoadSubjects(Result.Error(it)))
                },
                {
                    setState(State.LoadSubjects(Result.Success(it)))
                }
            )
        }
    }

    private fun updateEvents() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            val result = withContext(Dispatchers.IO) { upcomingEventsUseCase.run(calendar.time) }
            result.fold(
                {
                    setState(State.LoadCalendar(Result.Error(it)))
                },
                {
                    setState(State.LoadCalendar(Result.Success(it)))
                }
            )
        }
    }

    private fun updateFeed() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { getFeedUseCase.run() }
            result.fold(
                {
                    setState(State.LoadFeed(Result.Error(it)))
                },
                {
                    setState(
                        State.LoadFeed(
                            Result.Success(
                                it.map { card ->
                                    HomeCard(
                                        card.context,
                                        card.title,
                                        card.body,
                                        card.imageUrl,
                                        card.cardTemplate,
                                        card.primaryAction1,
                                        card.primaryAction2
                                    )
                                }
                            )
                        )
                    )
                }
            )
        }
    }
}
