package co.tuister.uisers.utils

import android.content.res.Resources
import androidx.databinding.InverseMethod
import co.tuister.uisers.R
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {

        fun stringToDate(string: String?): Date? {
            return string?.let {
                try {
                    DATE_FORMAT.parse(it)
                } catch (e: Exception) {
                    null
                }
            }
        }

        fun dateToString(date: Date?): String? {
            return date?.let {
                DATE_FORMAT.format(it)
            }
        }

        fun minutesToString(resources: Resources, timeInMinutes: Int): String {

            val minutes = timeInMinutes % 60
            val hours = ((timeInMinutes - minutes) / 60) % 24
            val days = (timeInMinutes - (hours * 60) - minutes) / (60 * 24)

            val builder = StringBuilder()

            if (days > 0)
                builder.append(resources.getQuantityString(R.plurals.days, days, days))
            if (hours > 0)
                builder.append(resources.getQuantityString(R.plurals.hours, hours, hours))
            if (minutes > 0)
                builder.append(resources.getQuantityString(R.plurals.minutes, minutes, minutes))

            return builder.toString()
        }

        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm")
    }
}

object DateConverter {

    @InverseMethod("stringToDate")
    @JvmStatic
    fun dateToString(value: Long): String? {
        return if (value == 0L) "" else {
            DateUtils.dateToString(Date(value))
        }
    }

    @JvmStatic
    fun stringToDate(value: String?): Long {
        return DateUtils.stringToDate(value)?.time ?: 0
    }
}
