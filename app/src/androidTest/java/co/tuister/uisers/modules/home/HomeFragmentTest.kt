package co.tuister.uisers.modules.home

import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.tuister.uisers.BaseEspressoTest
import co.tuister.uisers.R
import co.tuister.uisers.utils.CustomMatchers.waitViewIsDisplayed
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest : BaseEspressoTest() {

    @Test
    fun checkHomeLoadsSuccessfully() {
        waitViewIsDisplayed(withId(R.id.text_view_header_date))
        waitViewIsDisplayed(withId(R.id.content_view_schedule))
        waitViewIsDisplayed(withId(R.id.content_view_tasks))
    }
}
