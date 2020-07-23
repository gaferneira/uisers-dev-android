package co.tuister.uisers.modules.widgets

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import co.tuister.domain.base.getOrElse
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.usecases.career.GetScheduleUseCase
import co.tuister.uisers.R
import co.tuister.uisers.utils.DateUtils
import co.tuister.uisers.utils.extensions.getKoinInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking

class ScheduleWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ScheduleRemoteViewsFactory(this.applicationContext, intent)
    }
}

class ScheduleRemoteViewsFactory(
    private val context: Context,
    private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private var map: Map<String, List<SchedulePeriod>?> = mapOf()
    private var list: List<String> = listOf()

    override fun onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_item_schedule_loading)
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onDataSetChanged() {
        runBlocking {
            fetchData()
        }
    }

    private suspend fun fetchData() {
        val useCase = getKoinInstance<GetScheduleUseCase>()
        val data = useCase().getOrElse(listOf())
        map = data.groupBy { it.initialHour }
        list = map.entries.map { it.key }.sorted()
    }

    override fun hasStableIds() = false

    override fun getViewAt(position: Int): RemoteViews {

        val initialHour = list[position]
        val days = map[initialHour] ?: listOf()

        val view = RemoteViews(context.packageName, R.layout.widget_item_schedule)

        days.forEach {
            val index = calculateIndex(it.day)
            view.setTextViewText(descriptionIds[index], it.description)
            view.setTextViewText(hourIds[index], it.initialHour)
            view.setTextViewText(hourFinalIds[index], it.finalHour)
            view.setTextViewText(placeIds[index], it.place)
            val backgroundColor = context.resources.getIntArray(R.array.colors_100)[it.materialColor]
            view.setInt(containersIds[index], "setBackgroundColor", backgroundColor)
        }

        val emptyBlocks = List(DateUtils.DAYS_WEEK) { it } subtract days.map { calculateIndex(it.day) }
        emptyBlocks.forEach { index ->
            view.setTextViewText(descriptionIds[index], "")
            view.setTextViewText(hourIds[index], "")
            view.setTextViewText(hourFinalIds[index], "")
            view.setTextViewText(placeIds[index], "")
            view.setInt(containersIds[index], "setBackgroundColor", Color.WHITE)
        }

        return view
    }

    private fun calculateIndex(day: Int) = (day - 1) % DateUtils.DAYS_WEEK

    override fun getCount(): Int {
        return list.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
        scope.cancel()
    }

    companion object {

        val descriptionIds = listOf(
            R.id.text_view_sunday, R.id.text_view_monday, R.id.text_view_tuesday, R.id.text_view_wednesday,
            R.id.text_view_thursday, R.id.text_view_friday, R.id.text_view_saturday
        )

        val containersIds = listOf(
            R.id.container_sunday, R.id.container_monday, R.id.container_tuesday, R.id.container_wednesday,
            R.id.container_thursday, R.id.container_friday, R.id.container_saturday
        )

        val hourIds = listOf(
            R.id.text_view_hour_sunday, R.id.text_view_hour_monday, R.id.text_view_hour_tuesday, R.id.text_view_hour_wednesday,
            R.id.text_view_hour_thursday, R.id.text_view_hour_friday, R.id.text_view_hour_saturday
        )

        val hourFinalIds = listOf(
            R.id.text_view_hour_final_sunday, R.id.text_view_hour_final_monday, R.id.text_view_hour_final_tuesday,
            R.id.text_view_hour_final_wednesday, R.id.text_view_hour_final_thursday, R.id.text_view_hour_final_friday,
            R.id.text_view_hour_final_saturday
        )

        val placeIds = listOf(
            R.id.text_view_place_sunday, R.id.text_view_place_monday, R.id.text_view_place_tuesday, R.id.text_view_place_wednesday,
            R.id.text_view_place_thursday, R.id.text_view_place_friday, R.id.text_view_place_saturday
        )
    }
}
