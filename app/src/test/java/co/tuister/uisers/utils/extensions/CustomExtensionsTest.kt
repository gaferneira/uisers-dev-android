package co.tuister.uisers.utils.extensions

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import co.tuister.uisers.MockApplication
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P], application = MockApplication::class)
@RunWith(RobolectricTestRunner::class)
class CustomExtensionsTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test fun `Check setTextOrGone extension works as expected`() {

        val textView = TextView(context)

        textView.setTextOrGone("")
        assertThat(textView.visibility).isEqualTo(View.GONE)

        textView.setTextOrGone("Some text")
        assertThat(textView.visibility).isEqualTo(View.VISIBLE)
        assertThat(textView.text).isEqualTo("Some text")

        textView.setTextOrGone(null)
        assertThat(textView.visibility).isEqualTo(View.GONE)
    }
}
