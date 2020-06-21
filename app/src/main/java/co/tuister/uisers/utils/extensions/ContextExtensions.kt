package co.tuister.uisers.utils.extensions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import co.tuister.uisers.R
import java.util.*

fun Context.pickDateTime(calendar: Calendar, onSelectDateTime: ((calendar: Calendar) -> Unit)) {
    val startYear = calendar.get(Calendar.YEAR)
    val startMonth = calendar.get(Calendar.MONTH)
    val startDay = calendar.get(Calendar.DAY_OF_MONTH)
    val startHour = calendar.get(Calendar.HOUR_OF_DAY)
    val startMinute = calendar.get(Calendar.MINUTE)

    DatePickerDialog(
        this,
        DatePickerDialog.OnDateSetListener { _, year, month, day ->
            TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    val pickedDateTime = Calendar.getInstance()
                    pickedDateTime.set(year, month, day, hour, minute)
                    onSelectDateTime(pickedDateTime)
                },
                startHour, startMinute, false
            ).show()
        },
        startYear, startMonth, startDay
    ).show()
}

fun Context.pickTime(hours: Int, minutes: Int, onSelectTime: ((hours: Int, minutes: Int) -> Unit)) {
    TimePickerDialog(
        this,
        TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            onSelectTime(hour, minute)
        },
        hours, minutes, false
    ).show()
}

fun Context.checkRequireFormFields(vararg fields: TextView, showError: Boolean = true): Boolean {

    fields.forEach {
        it.error = null
    }

    var success = true
    fields.forEach {
        if (it.text.isNullOrEmpty()) {
            if (showError) {
                it.error = getString(R.string.error_field_required)
            }
            if (success) {
                success = false
                if (showError) {
                    it.requestFocus()
                }
            }
        }
    }

    return success
}
