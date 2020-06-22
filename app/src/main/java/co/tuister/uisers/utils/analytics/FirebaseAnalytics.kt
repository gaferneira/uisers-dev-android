package co.tuister.uisers.utils.analytics

import android.app.Activity
import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import java.util.Date

class FirebaseAnalytics(
    context: Context
) : Analytics {

    private val instance = FirebaseAnalytics.getInstance(context)

    override fun trackScreenView(activity: Activity, screenName: String, screenClassName: String) {
        instance.setCurrentScreen(activity, screenName, screenClassName)
    }

    override fun trackUserLogin() {
        instance.logEvent(Analytics.EVENT_LOGIN) {
            param("date", getCurrentDate())
        }
    }

    override fun trackUserLogout() {
        instance.logEvent(Analytics.EVENT_LOGOUT) {
            param("date", getCurrentDate())
        }
    }

    override fun trackUserSignUp() {
        instance.logEvent(Analytics.EVENT_SIGN_UP) {
            param("date", getCurrentDate())
        }
    }

    override fun trackForgotPassword() {
        instance.logEvent(Analytics.EVENT_FORGOT_PASSWORD) {
            param("date", getCurrentDate())
        }
    }

    override fun trackEvent(eventName: String) {
        instance.logEvent(eventName) {
            param("date", getCurrentDate())
        }
    }

    private fun getCurrentDate(): String {
        return Date().toString()
    }
}
