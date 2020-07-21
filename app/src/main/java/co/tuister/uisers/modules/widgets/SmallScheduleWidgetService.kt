package co.tuister.uisers.modules.widgets

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import co.tuister.domain.base.getOrElse
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.usecases.career.GetScheduleUseCase
import co.tuister.uisers.R
import co.tuister.uisers.utils.DateUtils.Companion.DAYS_WEEK
import co.tuister.uisers.utils.extensions.getKoinInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext, intent)
    }
}

class StackRemoteViewsFactory(
    private val context: Context,
    private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private var list: List<Pair<Int?, SchedulePeriod?>> = listOf()

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
        val items: MutableList<Pair<Int?, SchedulePeriod?>> = mutableListOf()
        data.groupBy { it.day }.forEach { entry ->
            items.add(Pair(entry.key, null))
            items.addAll(entry.value.sortedBy { it.initialHour }.map { Pair(null, it) })
        }
        list = items
    }

    override fun hasStableIds() = false

    private fun getItemViewType(position: Int): Int {
        return if (list[position].first != null) VIEW_TYPE_HEADER else 1
    }

    override fun getViewAt(position: Int): RemoteViews {

        val pair = list[position]
        return if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            RemoteViews(context.packageName, R.layout.widget_item_small_schedule_title).also {
                val calendar = Calendar.getInstance()
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                calendar.set(Calendar.DAY_OF_WEEK, pair.first!!)
                val today = if (dayOfWeek == pair.first!! % DAYS_WEEK) " (Today)" else ""
                val text = "" + DateFormat.format("EEEE", calendar.time) + today
                it.setTextViewText(R.id.text_view_day, text)
            }
        } else {
            RemoteViews(context.packageName, R.layout.widget_item_small_schedule).also {
                with(pair.second!!) {
                    it.setTextViewText(R.id.text_view_class_name, description)
                    it.setTextViewText(R.id.text_view_description, place)
                    it.setTextViewText(R.id.text_view_hour, "$initialHour-$finalHour")
                    val backgroundColor = context.resources.getIntArray(R.array.colors_100)[materialColor]
                    it.setInt(R.id.container_widget_item, "setBackgroundColor", backgroundColor)
                }
            }
        }
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun onDestroy() {
        scope.cancel()
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
    }
}
