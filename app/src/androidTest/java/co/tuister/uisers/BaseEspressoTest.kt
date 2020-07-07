package co.tuister.uisers

import androidx.annotation.CallSuper
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.tuister.uisers.modules.login.LoginActivity
import co.tuister.uisers.modules.main.MainActivity
import co.tuister.uisers.utils.CustomMatchers.softCheckMatcher
import co.tuister.uisers.utils.Wait
import org.junit.Before
import org.junit.Rule

open class BaseEspressoTest {

    @get:Rule
    internal var activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    @CallSuper
    open fun setUp() {
        Wait.waitForCondition(message = "Main Activity didn't load in time") {
            when {
                softCheckMatcher(withId(R.id.bottom_nav_view), ViewMatchers.isCompletelyDisplayed())
                -> {
                    return@waitForCondition true
                }
                softCheckMatcher(withId(R.id.login_form), ViewMatchers.isCompletelyDisplayed())
                -> {
                    login()
                    return@waitForCondition true
                }
                else -> {
                }
            }
        }
    }

    private fun login() {
        Espresso.onView(withId(R.id.loginEmail)).perform(ViewActions.typeText("test@uisers.com"))
        Espresso.onView(withId(R.id.loginPassword))
            .perform(ViewActions.typeText("passpasspass"), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.login_sign_in_button)).perform(ViewActions.click())

        Wait.forActivity<MainActivity>()
    }
}
