package co.tuister.uisers.modules.home

import co.tuister.domain.entities.SubjectClass
import co.tuister.domain.entities.Task
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.utils.Result

sealed class HomeState<out T : Any>(result: Result<T>) : BaseState<T>(result) {
    class LoadHeader(result: Result<HomeHeader>) : HomeState<HomeHeader>(result)
    class LoadTasks(result: Result<List<Task>>) : HomeState<List<Task>>(result)
    class LoadSubjects(result: Result<List<SubjectClass>>) : HomeState<List<SubjectClass>>(result)
}

open class HomeData(val template: HomeAdapter.HomeEnum)

data class HomeHeader(
    var career: String?,
    var period: String?
) : HomeData(HomeAdapter.HomeEnum.HEADER)

data class HomeSubjects(var list: List<SubjectClass>?) : HomeData(HomeAdapter.HomeEnum.SUBJECT)

data class HomeTasks(var list: List<Task>?) : HomeData(HomeAdapter.HomeEnum.TASKS)
