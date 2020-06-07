package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(
    var title: String = "",
    var description: String? = null,
    var dueDate: Long? = null,
    //DO, DOING, DONE
    var status: Int = 0,
    var reminder: Int? = null
) : Parcelable {}