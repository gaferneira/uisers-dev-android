package co.tuister.uisers.services

import android.content.Intent
import android.net.Uri
import co.tuister.uisers.modules.login.LoginActivity
import co.tuister.uisers.modules.main.MainActivity
import co.tuister.uisers.utils.NotificationsUtil
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
                NotificationsUtil.showNotification(applicationContext,
                    id, it.title, it.body, Intent(this, MainActivity::class.java))
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

        NotificationsUtil.showNotification(applicationContext, notificationId, title, description, newIntent)
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

    companion object {
        private const val NOTIFICATION_ID = "id"
        private const val NOTIFICATION_TITLE = "t"
        private const val NOTIFICATION_DESCRIPTION = "d"
        private const val NOTIFICATION_URL = "u"
        private const val NOTIFICATION_DEEP_LINK = "dl"
    }
}
