package co.tuister.uisers.utils

import android.util.Log
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.hamcrest.Matcher
import java.time.Clock
import java.time.Duration

class Wait(private val view: Matcher<View>, private var timeout: Long = 10_000L, private var pollingPeriod: Long = 100L) {

    companion object {
        const val globalTimeout = 30_000L
        val TAG = "TEST_" + this::class.java.canonicalName
        private val clock: Clock = Clock.systemDefaultZone()
        private val sleeper: Sleeper = Sleeper()

        inline fun <reified T> forActivity(timeoutMillis: Long = globalTimeout) {
            val activityClassName: String = T::class.java.name
            Log.d(TAG, "Waiting for: [$activityClassName]")
            if (activityClassName in getResumedActivityNames()) {
                Log.d(TAG, "[$activityClassName] is already loaded!")
                return
            } else {
                val monitor = getInstrumentation().addMonitor(activityClassName, null, false)
                if (monitor.waitForActivityWithTimeout(timeoutMillis) == null) {
                    throw RuntimeException("[$activityClassName] was not loaded within $timeoutMillis ms!")
                }
                getInstrumentation().removeMonitor(monitor)
            }
        }

        fun getResumedActivityNames(): MutableList<String> {
            val resumedActivities = mutableListOf<String>()
            getInstrumentation().runOnMainSync {
                for (activity in ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)) {
                    Log.d(TAG, "RESUMED activity: [${activity.javaClass.name}]")
                    resumedActivities.add(activity.javaClass.name)
                }
            }
            return resumedActivities
        }

        fun <T> waitForCondition(timeout: Long = globalTimeout, pollingPeriod: Long = 1000L, message: String, block: () -> T): T {
            val end = clock.instant().plus(Duration.ofMillis(timeout))
            var finished: T
            while (true) {
                finished = block()
                if (finished == true) {
                    break
                }
                if (end.isBefore(clock.instant())) {
                    throw RuntimeException(message)
                }
                try {
                    sleeper.sleep(Duration.ofMillis(pollingPeriod))
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    throw RuntimeException(message)
                }
            }
            return finished
        }
    }

    fun until(condition: ViewAssertion, timeout: Long = this.timeout, pollingPeriod: Long = this.pollingPeriod) {

        val end = clock.instant().plus(Duration.ofMillis(timeout))
        var lastException: Exception?
        var lastAssertionError: AssertionError?

        Log.d(TAG, "waiting for $timeout sec for condition $condition")
        while (true) {
            lastException = null
            lastAssertionError = null

            try {
                onView(view).check(condition)

                // Clear the last exception; if another retry or timeout exception would
                // be caused by a false or null value, the last exception is not the
                // cause of the timeout.
                lastException = null

                return
            } catch (e: Exception) {
                lastException = e
            } catch (ae: AssertionError) {
                lastAssertionError = ae
            }

            // Check the timeout after evaluating the function to ensure conditions with a zero timeout can succeed.
            if (end.isBefore(clock.instant())) {
                val timeoutMessage = "Expected condition failed:" +
                    "waiting for $condition (tried for $timeout second(s) with $pollingPeriod milliseconds interval)"

                throw RuntimeException(timeoutMessage, lastException ?: lastAssertionError)
            }

            try {
                sleeper.sleep(Duration.ofMillis(pollingPeriod))
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw RuntimeException(e)
            }
        }
    }
}
