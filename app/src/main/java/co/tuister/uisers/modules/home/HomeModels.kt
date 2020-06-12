package co.tuister.uisers.modules.home

import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.entities.Task

open class HomeData(val template: HomeAdapter.HomeEnum)

data class HomeHeader(
  var career: String?,
  var period: String?
) : HomeData(HomeAdapter.HomeEnum.HEADER)

data class HomeSchedule(var list: List<SchedulePeriod>?) : HomeData(HomeAdapter.HomeEnum.SCHEDULE)

data class HomeTasks(var list: List<Task>?) : HomeData(HomeAdapter.HomeEnum.TASKS)
