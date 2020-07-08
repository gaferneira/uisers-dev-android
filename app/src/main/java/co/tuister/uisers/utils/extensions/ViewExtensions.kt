package co.tuister.uisers.utils.extensions

import android.os.SystemClock
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible

private const val DELAY_MILLIS = 800

fun View.singleClick(
    onClickView: (View) -> Unit
) {
    setOnClickListener(object : View.OnClickListener {

        var lastClickTime: Long? = null

        override fun onClick(v: View?) {
            val time = SystemClock.elapsedRealtime()
            if (lastClickTime == null || time.minus(lastClickTime!!) >= DELAY_MILLIS) {
                lastClickTime = time
                onClickView(this@singleClick)
            }
        }
    })
}

fun TextView.setTextOrGone(string: String?) {
    text = string
    isVisible = !string.isNullOrBlank()
}
