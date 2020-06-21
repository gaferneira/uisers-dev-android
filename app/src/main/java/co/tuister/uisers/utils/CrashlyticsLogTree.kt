package co.tuister.uisers.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsLogTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            // Don't send those kind of logs
            return
        }

        FirebaseCrashlytics.getInstance().setCustomKey(CRASHLYTICS_KEY_PRIORITY, priority)
        FirebaseCrashlytics.getInstance().setCustomKey(CRASHLYTICS_KEY_MESSAGE, message)

        tag?.let {
            FirebaseCrashlytics.getInstance().setCustomKey(CRASHLYTICS_KEY_TAG, it)
        }

        if (t == null) {
            FirebaseCrashlytics.getInstance().recordException(Exception(message))
        } else {
            FirebaseCrashlytics.getInstance().recordException(t)
        }
    }

    companion object {
        private const val CRASHLYTICS_KEY_PRIORITY = "priority"
        private const val CRASHLYTICS_KEY_TAG = "tag"
        private const val CRASHLYTICS_KEY_MESSAGE = "message"
    }
}
