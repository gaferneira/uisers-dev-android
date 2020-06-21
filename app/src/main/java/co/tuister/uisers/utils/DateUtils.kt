package co.tuister.uisers.utils

import android.content.res.Resources
import androidx.databinding.InverseMethod
import co.tuister.uisers.R
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {

        fun stringToDateTime(string: String?): Date? {
            return string?.let {
                try {
                    DATE_TIME_FORMAT.parse(it)
                } catch (e: Exception) {
                    null
                }
            }
        }

        fun dateTimeToString(date: Date?, format: String? = null): String? {
            return date?.let {
                val sdf = format?.let { SimpleDateFormat(it) } ?: DATE_TIME_FORMAT
                sdf.format(it)
            }
        }

        fun dateToString(date: Date?, format: String? = null): String? {
            return date?.let {
                val sdf = format?.let { SimpleDateFormat(it) } ?: DATE_FORMAT
                sdf.format(it)
            }
        }

        fun stringToDate(string: String?, format: String? = null): Date? {
            return string?.let {
                try {
                    val sdf = format?.let { SimpleDateFormat(it) } ?: DATE_FORMAT
                    sdf.parse(it)
                } catch (e: Exception) {
                    null
                }
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

        private val DATE_TIME_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm")
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
    }
}

object DateConverter {

    @InverseMethod("stringToDate")
    @JvmStatic
    fun dateToString(value: Long): String? {
        return if (value == 0L) "" else {
            DateUtils.dateTimeToString(Date(value))
        }
    }

    @JvmStatic
    fun stringToDate(value: String?): Long {
        return DateUtils.stringToDateTime(value)?.time ?: 0
    }
}
