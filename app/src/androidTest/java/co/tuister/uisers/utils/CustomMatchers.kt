package co.tuister.uisers.utils

import android.util.Log
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher

object CustomMatchers {

    private val TAG = "TEST_" + CustomMatchers::class.java.canonicalName

    fun softCheckMatcher(view: Matcher<View>, matcher: Matcher<View>): Boolean {
        return try {
            onView(view).check(matches(matcher))
            Log.d(TAG, "View [$view] matching condition[$matcher] was found")
            true
        } catch (ae: AssertionError) {
            Log.d(TAG, "View [$view] matching condition[$matcher] was not found")
            false
        } catch (e: Exception) {
            Log.d(TAG, "View [$view] matching condition[$matcher] was not found")
            false
        }
    }

    fun waitViewIsDisplayed(view: Matcher<View>, timeout: Long = 10 * 1000L): Boolean {
        return try {
            Wait(view, timeout).until(matches(ViewMatchers.isDisplayed()))
            Log.d(TAG, "View $view was found")
            true
        } catch (e: RuntimeException) {
            Log.d(TAG, "View $view was not found")
            false
        }
    }
}
