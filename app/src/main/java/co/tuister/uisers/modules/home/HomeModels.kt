package co.tuister.uisers.modules.home

import co.tuister.domain.entities.Event
import co.tuister.domain.entities.FeedAction
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.entities.Task

open class HomeData(val template: HomeAdapter.HomeEnum)

data class HomeHeader(
    var career: String?,
    var period: String?
) : HomeData(HomeAdapter.HomeEnum.HEADER)

data class HomeSchedule(var list: List<SchedulePeriod>?) : HomeData(HomeAdapter.HomeEnum.SCHEDULE)

data class HomeTasks(var list: List<Task>?) : HomeData(HomeAdapter.HomeEnum.TASKS)

data class HomeCalendar(var list: List<Event>?) : HomeData(HomeAdapter.HomeEnum.CALENDAR)

data class HomeCard(
    var context: String? = null,
    var title: String? = null,
    var body: String? = null,
    var imageUrl: String? = null,
    var cardTemplate: String = "",
    val primaryAction1: FeedAction? = null,
    val primaryAction2: FeedAction? = null
) : HomeData(HomeAdapter.HomeEnum.CARD)
