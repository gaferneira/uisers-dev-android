package co.tuister.uisers.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import co.tuister.domain.entities.Task
import co.tuister.uisers.R
import co.tuister.uisers.modules.login.LoginActivity
import co.tuister.uisers.utils.NotificationsUtil
import kotlin.random.Random

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(ALARM_TITLE)
        val message = intent.getStringExtra(ALARM_MESSAGE)
        val deepLink = intent.getStringExtra(ALARM_DEEP_LINK)
        val id = Random.nextInt()
        val loginIntent = LoginActivity.createIntent(context, deepLink)
        NotificationsUtil.showNotification(context, id, title, message, loginIntent)
    }

    companion object {

        private const val ALARM_TITLE = "ALARM_TITLE"
        private const val ALARM_MESSAGE = "ALARM_MESSAGE"
        private const val ALARM_DEEP_LINK = "ALARM_DEEP_LINK"
        private const val MILLISECONDS_MINUTE = 60000

        fun createTaskAlarm(context: Context, task: Task) {

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra(ALARM_TITLE, task.title)
                putExtra(ALARM_MESSAGE, task.description)
                putExtra(ALARM_DEEP_LINK, context.getString(R.string.deep_link_tasks))
            }

            val dueDate = task.dueDate
            val reminder = task.reminder?.toLong()
            val currentTime = System.currentTimeMillis()
            val requestCode = task.id.hashCode()

            val timeInMillis = when {
                (dueDate == null || reminder == null) -> null
                currentTime > dueDate -> null
                else -> dueDate - (reminder * MILLISECONDS_MINUTE)
            }

            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
            if (timeInMillis != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                }
            } else {
                alarmManager.cancel(pendingIntent)
            }
        }
    }
}
