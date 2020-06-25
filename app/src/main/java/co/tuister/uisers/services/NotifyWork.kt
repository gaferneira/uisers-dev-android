package co.tuister.uisers.services

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker.Result.success
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import co.tuister.domain.entities.Task
import co.tuister.uisers.R
import co.tuister.uisers.modules.login.LoginActivity
import co.tuister.uisers.utils.NotificationsUtil
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class NotifyWork(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString(NOTIFICATION_TITLE)
        val message = inputData.getString(NOTIFICATION_MESSAGE)
        val deepLink = inputData.getString(NOTIFICATION_DEEP_LINK)
        val id = Random.nextInt()
        val intent = LoginActivity.createIntent(applicationContext, deepLink)
        NotificationsUtil.showNotification(applicationContext, id, title, message, intent)
        return success()
    }

    companion object {

        private const val NOTIFICATION_TITLE = "NOTIFICATION_TITLE"
        private const val NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE"
        private const val NOTIFICATION_DEEP_LINK = "NOTIFICATION_DEEP_LINK"

        fun scheduleTaskNotification(context: Context, task: Task) {

            val instanceWorkManager = WorkManager.getInstance(context)

            val dueDate = task.dueDate
            val reminder = task.reminder?.toLong()
            val currentTime = System.currentTimeMillis()

            if (dueDate == null || reminder == null) {
                instanceWorkManager.cancelUniqueWork(task.id)
                return
            } else if (currentTime > dueDate) {
                return
            }

            val delay = dueDate - currentTime - reminder

            val data = Data.Builder().putString(NOTIFICATION_TITLE, task.title)
                .putString(NOTIFICATION_MESSAGE, task.description)
                .putString(NOTIFICATION_DEEP_LINK, context.getString(R.string.deep_link_tasks))
                .build()

            val notificationWork = OneTimeWorkRequest.Builder(NotifyWork::class.java)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            instanceWorkManager.beginUniqueWork(task.id, ExistingWorkPolicy.REPLACE, notificationWork).enqueue()
        }
    }
}
