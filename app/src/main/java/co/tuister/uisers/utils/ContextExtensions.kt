package co.tuister.uisers.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.*

fun Context.pickDateTime(calendar: Calendar, onSelectDateTime: ((calendar: Calendar) -> Unit)) {
    val startYear = calendar.get(Calendar.YEAR)
    val startMonth = calendar.get(Calendar.MONTH)
    val startDay = calendar.get(Calendar.DAY_OF_MONTH)
    val startHour = calendar.get(Calendar.HOUR_OF_DAY)
    val startMinute = calendar.get(Calendar.MINUTE)

    DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            val pickedDateTime = Calendar.getInstance()
            pickedDateTime.set(year, month, day, hour, minute)
            onSelectDateTime(pickedDateTime)
        }, startHour, startMinute, false).show()
    }, startYear, startMonth, startDay).show()
}
