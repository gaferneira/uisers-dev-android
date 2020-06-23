package co.tuister.uisers.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import co.tuister.uisers.R
import co.tuister.uisers.modules.login.LoginActivity
import co.tuister.uisers.modules.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            handlePayload(remoteMessage.data)
        } else {
            // Check if message contains a notification payload.
            remoteMessage.notification?.let {
                val id: Int = Random.nextInt()
                showNotification(id, it.title, it.body, Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun handlePayload(data: Map<String, String>) {

        val title = data[NOTIFICATION_TITLE]
        val description = data[NOTIFICATION_DESCRIPTION]
        val url = data[NOTIFICATION_URL]
        val deepLink = data[NOTIFICATION_DEEP_LINK]
        val notificationId = data[NOTIFICATION_ID]?.toInt() ?: Random.nextInt()

        val newIntent = url?.let {
            try {
                var uri = Uri.parse(url)
                if (uri.scheme.isNullOrEmpty()) {
                    uri = Uri.parse("http://$url")
                }
                Intent(Intent.ACTION_VIEW, uri)
            } catch (e: Exception) {
                null
            }
        } ?: LoginActivity.createIntent(baseContext, deepLink)

        showNotification(notificationId, title, description, newIntent)
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
    }
    // [END on_new_token]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun showNotification(id: Int, title: String?, messageBody: String?, intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id, notificationBuilder.build())
    }

    companion object {
        private const val NOTIFICATION_ID = "id"
        private const val NOTIFICATION_TITLE = "t"
        private const val NOTIFICATION_DESCRIPTION = "d"
        private const val NOTIFICATION_URL = "u"
        private const val NOTIFICATION_DEEP_LINK = "dl"
    }
}
