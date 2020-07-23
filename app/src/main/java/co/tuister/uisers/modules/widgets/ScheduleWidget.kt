package co.tuister.uisers.modules.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import co.tuister.uisers.R
import co.tuister.uisers.modules.login.LoginActivity
import co.tuister.uisers.utils.DateUtils
import java.util.Calendar

/**
 * Implementation of App Widget functionality.
 */
class ScheduleWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == intent?.action) {
            val mgr = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, ScheduleWidget::class.java)
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.listview)
        }
        super.onReceive(context, intent)
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

        val views = RemoteViews(context.packageName, R.layout.widget_schedule)

        val clickIntent = LoginActivity.createIntent(context, context.getString(R.string.deep_link_career))
        val startActivityPendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setPendingIntentTemplate(R.id.listview, startActivityPendingIntent)

        val adapterIntent = Intent(context, ScheduleWidgetService::class.java).apply {
            // Add the app widget ID to the intent extras.
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
        }

        views.setRemoteAdapter(R.id.listview, adapterIntent)

        // Setup update button to send an update request as a pending intent.
        val intentUpdate = Intent(context, ScheduleWidget::class.java).apply {
            // The intent action must be an app widget update.
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

            // Include the widget ID to be updated as an intent extra.
            val idArray = intArrayOf(appWidgetId)
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray)
        }

        val days = List(DateUtils.DAYS_WEEK) { it + 1 }
        val textViews = listOf(
            R.id.text_view_title_sunday, R.id.text_view_title_monday, R.id.text_view_title_tuesday,
            R.id.text_view_title_wednesday, R.id.text_view_title_thursday, R.id.text_view_title_friday, R.id.text_view_title_saturday
        )

        textViews.forEachIndexed { index, view ->
            val day = getDayWeek(days[index])
            views.setTextViewText(view, day)
        }

        // Wrap it all in a pending intent to send a broadcast.
        // Use the app widget ID as the request code (third argument) so that
        // each intent is unique.
        val pendingUpdate = PendingIntent.getBroadcast(
            context,
            appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Assign the pending intent to the button onClick handler
        views.setOnClickPendingIntent(R.id.button_update, pendingUpdate)
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getDayWeek(day: Int): String? {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, day)
        }
        return DateUtils.dateToString(calendar.time, "EEE")
    }
}
